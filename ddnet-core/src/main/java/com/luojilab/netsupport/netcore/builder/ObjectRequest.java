package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luojilab.netsupport.netcore.datasource.MemoryDataPool;
import com.luojilab.netsupport.netcore.datasource.database.TableDef;
import com.luojilab.netsupport.netcore.domain.strategy.base.ObjectGetStrategy;
import com.luojilab.netsupport.netcore.domain.strategy.object.OnlyNetForObject;
import com.luojilab.netsupport.netcore.domain.strategy.object.RefreshCacheForObject;
import com.luojilab.netsupport.netcore.domain.strategy.object.Level3CacheForObject;

import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_ONLY_NET;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_REFRESH_CACHE;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_LEVEL3_CACHE;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_NONE;

/**
 * Created by liushuo on 2017/5/26.
 * 通用对象请求类，可配置
 */

public class ObjectRequest<T> extends BaseRequest<T> {
    //缓存数据相关参数
    private String mCacheId;

    /**
     * 调用顺序为配置语句的最后
     *
     * @param cacheId
     */
    public void setCacheId(@Nullable String cacheId) {
        mCacheId = cacheId;

        if (TextUtils.isEmpty(mCacheId)) {
            checkCacheConflict();
        }
    }

    /**
     * cacheId 为空，需要执行该方法验证合理性
     */
    private void checkCacheConflict() {
        if (mCacheMemory || mCacheDB) {
            throw new IllegalArgumentException("参数错误，cache id 为空，无法指定cache db/memory");
        }

        if (mDefaultRequestStrategy == STRATEGY_REFRESH_CACHE || mDefaultRequestStrategy == STRATEGY_LEVEL3_CACHE) {
            throw new IllegalArgumentException("参数错误，cache id 为空，无法指定cache 相关的请求策略");
        }
        if (mExpireRequestStrategy == STRATEGY_REFRESH_CACHE || mExpireRequestStrategy == STRATEGY_LEVEL3_CACHE) {
            throw new IllegalArgumentException("参数错误，cache id 为空，无法指定cache 相关的过期请求策略");
        }
    }

    /**
     * 如果k表示Bean，则dataClass 与k为同一个类型
     * 如何k 表示List<Bean>,则dataClass 与Bean为同一个类型
     *
     * @param dataClass
     */
    public ObjectRequest(Class<T> dataClass) {
        super(dataClass);
    }

    @Nullable
    @Override
    protected JsonElement adaptStructForCache(@NonNull JsonElement v) {
        JsonObject objectContent = (JsonObject) v;

        if (objectContent.size() == 0) return objectContent;//服务器返回空
        if (mParseKeys.isEmpty()) {
            appendCacheId(objectContent);
            return objectContent;
        }

        mExtraResult = objectContent; // 记录content句柄，以便持有除parse keys之外的返回内容

        int count = mParseKeys.size();
        for (int i = 0; i < count; i++) {
            String segment = mParseKeys.get(i);

            JsonElement je = (i == count - 1) ? objectContent.remove(segment) : objectContent.get(segment);
            if (je == null || je instanceof JsonNull) return JsonNull.INSTANCE;

            if (je instanceof JsonObject) {
                objectContent = (JsonObject) je;
            } else {
                throw new IllegalArgumentException("请求字段对应的数据类型不是JsonObject.--->" + segment);
            }
        }

        appendCacheId(objectContent);

        return objectContent;
    }

    private void appendCacheId(@NonNull JsonObject v) {
        if (!TextUtils.isEmpty(mCacheId)) {
            v.addProperty(TableDef.TableNosql.Column.COLUMN_ID, mCacheId);
        }
    }

    @Override
    protected void cache2Memory(@NonNull T data) {
        if (mCacheMemory) {
            MemoryDataPool.getInstance().cacheObjectDataInMemory(mDataClass, mCacheId, data);
        }
    }

    @NonNull
    @Override
    protected TypeToken<T> getTypeToken() {
        return TypeToken.get(mDataClass);
    }

    private ObjectGetStrategy getObjectGetStrategy(int requestStrategy) {
        switch (requestStrategy) {
            case STRATEGY_LEVEL3_CACHE:
                return new Level3CacheForObject(mDataClass, mCacheId, getCall(), this);
            case STRATEGY_REFRESH_CACHE:
                return new RefreshCacheForObject(mDataClass, mCacheId, getCall(), this);
            case STRATEGY_ONLY_NET:
                return new OnlyNetForObject(getCall(), this);
            default:
                throw new IllegalArgumentException("指定了非法的请求策略，requestDefaultStrategy:" + requestStrategy);
        }

    }

    @Override
    public void perform() {
        super.perform();

        if (!isResponseValid() && mExpireRequestStrategy != STRATEGY_NONE) {
            ObjectGetStrategy ogs = getObjectGetStrategy(mExpireRequestStrategy);
            ogs.fetchData();
        }


        //配置默认请求策略
        ObjectGetStrategy strategy = getObjectGetStrategy(mDefaultRequestStrategy);
        strategy.fetchData();
    }
}
