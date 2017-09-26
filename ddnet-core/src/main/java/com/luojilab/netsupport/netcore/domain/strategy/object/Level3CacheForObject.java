package com.luojilab.netsupport.netcore.domain.strategy.object;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.MemoryStorage;
import com.luojilab.netsupport.netcore.datasource.sync.NoSqlSyncStrategy;
import com.luojilab.netsupport.netcore.datasource.DALFactory;
import com.luojilab.netsupport.netcore.domain.ResponseListener;
import com.luojilab.netsupport.netcore.domain.strategy.base.ObjectGetStrategy;
import com.google.gson.JsonObject;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class Level3CacheForObject<T> extends ObjectGetStrategy<T> {
    private MemoryStorage mMemoryDataPool;

    public Level3CacheForObject(@NonNull Class<T> dataClass, @NonNull String id, @NonNull Call<JsonObject> call, @Nullable ResponseListener<T> callback) {
        super(dataClass, id, call, callback);
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(call);

        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
    }


    @Override
    public void fetchData() {
        T data = mMemoryDataPool.getObjectDataCached(mDataClass, mId);

        if (data != null) {

            notifyCacheSuccess(data, true);

        } else {
            //执行网络之前，回调reqeust
            if (mResponseListener != null) {
                mResponseListener.preNetRequest();
            }

            invokeRetrofit();
        }

    }

    private void notifyCacheSuccess(@NonNull T data, boolean isDone) {
        if (mResponseListener != null) {
            mResponseListener.onCacheResponse(data, isDone);
        }
    }
}
