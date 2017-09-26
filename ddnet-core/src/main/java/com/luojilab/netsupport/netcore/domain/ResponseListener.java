package com.luojilab.netsupport.netcore.domain;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.google.gson.JsonObject;

/**
 * Created by liushuo on 16/3/17.
 */
public interface ResponseListener<T> {

    void onCacheResponse(@NonNull T t, boolean isDone);

    /**
     * 执行网络请求之前策略需要回调接口通知request执行相应操作(eg.弹窗加载对话框)
     */
    void preNetRequest();

    void onRetrofitResponse(@NonNull JsonObject header, @NonNull JsonElement content);


    void onNetError(@NonNull ResponseHeader httpResponse);

    void onNetCanceled();
}
