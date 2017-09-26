package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luojilab.netsupport.netcore.datasource.MemoryDataPool;
import com.luojilab.netsupport.netcore.domain.strategy.array.OnlyNetForArray;
import com.luojilab.netsupport.netcore.domain.strategy.array.RefreshCacheForArray;
import com.luojilab.netsupport.netcore.domain.strategy.array.Level3CacheForArray;
import com.luojilab.netsupport.netcore.domain.strategy.base.ArrayGetStrategy;

import java.util.Arrays;

import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_ONLY_NET;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_REFRESH_CACHE;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_LEVEL3_CACHE;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_NONE;

/**
 * Created by liushuo on 2017/5/26.
 * 通用对象列表请求类，可配置
 */

public class ArrayRequest<T> extends BaseRequest<T[]> {
    /**
     * 如果k表示Bean，则dataClass 与k为同一个类型
     * 如何k 表示List<Bean>,则dataClass 与Bean为同一个类型
     *
     * @param dataClass
     */
    public ArrayRequest(Class<T> dataClass) {
        super(dataClass);
    }

    @Nullable
    @Override
    protected JsonElement adaptStructForCache(@NonNull JsonElement v) {
        if (mParseKeys.isEmpty()) return v;

        JsonObject objectResult = (JsonObject) v;

        mExtraResult = objectResult; // 记录content句柄，以便持有除parse keys之外的返回内容

        int count = mParseKeys.size();
        for (int i = 0; i < count; i++) {
            String segment = mParseKeys.get(i);

            boolean isLast = i == count - 1;
            JsonElement je = isLast ? objectResult.remove(segment) : objectResult.get(segment);
            if (je == null || je instanceof JsonNull) return JsonNull.INSTANCE;

            if (isLast) return je;

            if (je instanceof JsonObject) {
                objectResult = (JsonObject) je;
            } else {
                throw new IllegalArgumentException("请求字段对应的数据类型不是JsonObject.--->" + segment);
            }
        }

        return objectResult;
    }

    @Override
    protected void cache2Memory(@NonNull T[] data) {
        if (mCacheMemory) {
            MemoryDataPool.getInstance().cacheArrayDataInMemory(mDataClass, Arrays.asList(data));
        }
    }

    @NonNull
    @Override
    protected TypeToken<T[]> getTypeToken() {
        return (TypeToken<T[]>) TypeToken.getArray(mDataClass);
    }

    private ArrayGetStrategy getArrayGetStrategy(int requestStrategy) {
        switch (requestStrategy) {
            case STRATEGY_LEVEL3_CACHE:
                return new Level3CacheForArray<>(mDataClass, getCall(), this);
            case STRATEGY_REFRESH_CACHE:
                return new RefreshCacheForArray(mDataClass, getCall(), this);
            case STRATEGY_ONLY_NET:
                return new OnlyNetForArray(getCall(), this);
            default:
                throw new IllegalArgumentException("指定了非法的请求策略，requestDefaultStrategy:" + requestStrategy);
        }

    }


    @Override
    public void perform() {
        super.perform();

        if (!isResponseValid() && mExpireRequestStrategy != STRATEGY_NONE) {
            ArrayGetStrategy ogs = getArrayGetStrategy(mExpireRequestStrategy);
            ogs.fetchData();
        }


        //配置默认请求策略
        ArrayGetStrategy strategy = getArrayGetStrategy(mDefaultRequestStrategy);
        strategy.fetchData();
    }
}
