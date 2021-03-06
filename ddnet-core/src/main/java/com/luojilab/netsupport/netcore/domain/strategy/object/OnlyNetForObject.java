package com.luojilab.netsupport.netcore.domain.strategy.object;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.ResponseListener;
import com.luojilab.netsupport.netcore.domain.strategy.base.ObjectGetStrategy;
import com.google.gson.JsonObject;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class OnlyNetForObject<T> extends ObjectGetStrategy<T> {

    public OnlyNetForObject(@NonNull Call<JsonObject> call, @Nullable ResponseListener<T> callback) {
        super(null, null, call, callback);
        Preconditions.checkNotNull(call);
    }


    @Override
    public void fetchData() {

        //执行网络之前，回调reqeust
        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        invokeRetrofit();
    }
}