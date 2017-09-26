package com.luojilab.netsupport.netcore.domain.strategy.array;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.luojilab.netsupport.netcore.datasource.DALFactory;
import com.luojilab.netsupport.netcore.datasource.base.MemoryStorage;
import com.luojilab.netsupport.netcore.datasource.sync.NoSqlSyncStrategy;
import com.luojilab.netsupport.netcore.domain.ResponseListener;
import com.luojilab.netsupport.netcore.domain.strategy.base.ArrayGetStrategy;
import com.luojilab.netsupport.netcore.utils.CoreUtils;

import java.util.List;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class RefreshCacheForArray<T> extends ArrayGetStrategy<T> {
    private MemoryStorage mMemoryDataPool;

    public RefreshCacheForArray(@NonNull Class<T> dataClass, @NonNull Call<JsonObject> call, @Nullable ResponseListener<T[]> listener) {
        super(dataClass, call, listener);
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(call);

        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
    }


    @Override
    public void fetchData() {
        List<T> data = mMemoryDataPool.getArrayDataCached(mDataClass);

        if (data != null && data.size() > 0) {

            T[] typeArr = CoreUtils.createGenericArray(mDataClass, data.size());
            T[] dataArr = data.toArray(typeArr);

            notifyCacheSuccess(dataArr, false);

        }

        //执行网络之前，回调reqeust
        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        invokeRetrofit();

    }

    private void notifyCacheSuccess(@NonNull T[] data, boolean isDone) {
        if (mResponseListener != null) {
            mResponseListener.onCacheResponse(data, isDone);
        }
    }

}
