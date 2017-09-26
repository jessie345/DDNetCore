package com.luojilab.netsupport.netcore.domain.strategy.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.ResponseListener;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 * the object is json object
 */
public abstract class ArrayGetStrategy<T> extends RetrofitStrategy {
    protected ResponseListener<T[]> mResponseListener;
    protected Class<T> mDataClass;

    public ArrayGetStrategy(@Nullable Class<T> dataClass, @NonNull Call<JsonObject> call, @Nullable ResponseListener<T[]> listener) {
        super(call);
        Preconditions.checkNotNull(call);

        this.mResponseListener = listener;
        mDataClass = dataClass;
    }

    @Override
    protected void notifyNetSuccess(@NonNull JsonObject header, @NonNull JsonElement content) {
        if (mResponseListener != null) {
            mResponseListener.onRetrofitResponse(header, content);
        }
    }

    @Override
    protected void notifyNetError(@NonNull ResponseHeader rb) {
        if (mResponseListener != null) {
            mResponseListener.onNetError(rb);
        }
    }

    @Override
    protected void notifyNetCanceled() {
        if (mResponseListener != null) {
            mResponseListener.onNetCanceled();
        }
    }

    public abstract void fetchData();
}
