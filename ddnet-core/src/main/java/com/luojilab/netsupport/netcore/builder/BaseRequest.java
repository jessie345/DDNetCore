package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.luojilab.netsupport.utils.NetLogger;
import com.luojilab.netsupport.netcore.datasource.DALFactory;
import com.luojilab.netsupport.netcore.domain.CacheDispatcher;
import com.luojilab.netsupport.netcore.domain.request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static com.luojilab.netsupport.netcore.builder.Constants.CONTENT_TYPE_FORM;
import static com.luojilab.netsupport.netcore.builder.Constants.CONTENT_TYPE_JSON;
import static com.luojilab.netsupport.netcore.builder.Constants.HTTP_METHOD_GET;
import static com.luojilab.netsupport.netcore.builder.Constants.HTTP_METHOD_POST;

/**
 * Created by liushuo on 2017/5/26.
 * 通用对象请求类，可配置
 */

public abstract class BaseRequest<T> extends Request<T> {
    private static final String TAG = "BaseRequest";

    protected int mHttpMethod = Constants.HTTP_METHOD_POST;//http 请求方法
    protected int mDefaultRequestStrategy = Constants.STRATEGY_ONLY_NET;//数据默认(响应未过期)请求策略
    protected int mExpireRequestStrategy = Constants.STRATEGY_NONE;//数据过期请求策略,默认-1，无请求方案
    protected int mContentType = CONTENT_TYPE_JSON;

    protected String mUrl;//请求地址

    //缓存数据相关参数
    protected boolean mCacheMemory;
    protected boolean mCacheDB;

    //解析返回对象的字段层级
    protected List<String> mParseKeys = new ArrayList<>();

    protected JsonObject mExtraResult;

    /**
     * 返回除parsed keys 之外的数据
     *
     * @return
     */
    @Nullable
    public JsonObject getExtraResult() {
        return mExtraResult;
    }

    public int getExtraIntResult(int def, @NonNull Object... keys) {
        Preconditions.checkNotNull(keys);

        JsonPrimitive primitive = getExtraPrimitiveResult(keys);
        if (primitive == null) return def;

        return primitive.getAsInt();
    }

    public boolean getExtraBooleanResult(boolean def, @NonNull Object... keys) {
        Preconditions.checkNotNull(keys);

        JsonPrimitive primitive = getExtraPrimitiveResult(keys);
        if (primitive == null) return def;

        return primitive.getAsBoolean();
    }

    public String getExtraStringResult(String def, @NonNull Object... keys) {
        Preconditions.checkNotNull(keys);

        JsonPrimitive primitive = getExtraPrimitiveResult(keys);
        if (primitive == null) return def;

        return primitive.getAsString();
    }

    public double getExtraDoubleResult(double def, @NonNull Object... keys) {
        Preconditions.checkNotNull(keys);

        JsonPrimitive primitive = getExtraPrimitiveResult(keys);
        if (primitive == null) return def;

        return primitive.getAsDouble();
    }

    @Nullable
    public JsonPrimitive getExtraPrimitiveResult(@NonNull Object... keys) {
        Preconditions.checkNotNull(keys);

        if (mExtraResult == null) return null;
        JsonElement result = mExtraResult;

        int count = keys.length;
        for (int i = 0; i < count; i++) {
            Object key = keys[i];
            if (key == null) return null;

            if (key instanceof String) {
                result = ((JsonObject) result).get((String) key);
            } else if (key instanceof Integer) {
                result = ((JsonArray) result).get((Integer) key);
            } else {
                return null;
            }

            if (result == null) return null;
            if (i == count - 1) return result.getAsJsonPrimitive();
        }

        return null;
    }


    public void setContentType(@Constants.ContentType int contentType) {
        mContentType = contentType;
    }

    //配置请求参数
    public void setHttpMethod(@Constants.HttpMethod int method) {
        mHttpMethod = method;
    }

    public void setUrl(@NonNull String url) {
        Preconditions.checkNotNull(url);

        mUrl = url;
    }

    public void setCacheMemory(boolean cacheMemory) {
        mCacheMemory = cacheMemory;
    }

    public void setCacheDB(boolean cacheDB) {
        mCacheDB = cacheDB;
    }

    public void setRespExpire(long time) {
        super.setRespExpire(time);
    }

    public void setDefaultRequestStrategy(@Constants.RequestStrategy int strategy) {
        mDefaultRequestStrategy = strategy;
    }

    public void setExpireRequestStrategy(@Constants.RequestStrategy int strategy) {
        mExpireRequestStrategy = strategy;
    }

    public void setObjectParseKeys(@NonNull List<String> segments) {
        Preconditions.checkNotNull(segments);

        mParseKeys.clear();
        mParseKeys.addAll(segments);
    }

    public void setRequestParameters(@NonNull Map<String, Object> parameters) {
        Preconditions.checkNotNull(parameters);

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (TextUtils.isEmpty(key)) continue;
            if (value == null) continue;

            if (value instanceof Boolean) {
                Boolean b = (Boolean) value;
                addNetParameter(key, b);
            } else if (value instanceof Character) {
                Character c = (Character) value;
                addNetParameter(key, c);
            } else if (value instanceof Number) {
                Number n = (Number) value;
                addNetParameter(key, n);
            } else if (value instanceof String) {
                String s = (String) value;
                addNetParameter(key, s);
            } else {
                NetLogger.d(TAG, "未知的参数类型,key=" + key + ",value=" + value);
            }
        }
    }

    /**
     * 如果k表示Bean，则dataClass 与k为同一个类型
     * 如何k 表示List<Bean>,则dataClass 与Bean为同一个类型
     *
     * @param dataClass
     */
    public BaseRequest(Class dataClass) {
        super(dataClass);
    }

    @NonNull
    @Override
    public Call<JsonObject> getCall() {
        Preconditions.checkArgument(mHttpMethod == HTTP_METHOD_GET || mHttpMethod == HTTP_METHOD_POST, "错误的请求类型");

        if (mHttpMethod == HTTP_METHOD_GET) {
            return DALFactory.getApiService(EmbedApiService.class).RetrofitGetApi(mUrl,CoreUtils.json2QueryMap(mParamsContainer));
        }

        //处理post请求相关逻辑
        switch (mContentType) {
            case CONTENT_TYPE_JSON:
                return DALFactory.getApiService(EmbedApiService.class).RetrofitPostBodyApi(mUrl,mParamsContainer);
            case CONTENT_TYPE_FORM:
                return DALFactory.getApiService(EmbedApiService.class).RetrofitPostFormApi(mUrl,CoreUtils.json2UrlEncodedMap(mParamsContainer));
            default:
                throw new IllegalArgumentException("post请求的Content类型错误:" + mContentType);
        }
    }

    @Nullable
    @Override
    protected abstract JsonElement adaptStructForCache(@NonNull JsonElement v);

    @Override
    protected abstract void cache2Memory(@NonNull T data);

    @Override
    protected void cache2DB(@NonNull JsonElement data) {
        if (mCacheDB) {
            CacheDispatcher.getInstance().dispatchDataCache(mDataClass, data);
        }
    }

    @NonNull
    @Override
    protected abstract TypeToken<T> getTypeToken();

    @CallSuper
    @Override
    public void perform() {
        super.perform();
    }
}
