package com.luojilab.netsupport.netcore.domain.strategy.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luojilab.netsupport.netcore.datasource.retrofit.HeaderSchema;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseSchema;
import com.luojilab.netsupport.netcore.datasource.retrofit.StatusCode;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.luojilab.netsupport.utils.NetLogger;
import com.luojilab.netsupport.utils.NetCoreInitializer;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by liushuo on 16/3/17.
 * the object is json object
 */
abstract class RetrofitStrategy {
    private static final int HTTP_OK = 200;

    private Call<JsonObject> mCall;


    RetrofitStrategy(@NonNull Call<JsonObject> call) {
        Preconditions.checkNotNull(call);

        mCall = call;
    }

    protected void invokeRetrofit() {
        Context context = NetCoreInitializer.getInstance().getAppContext();
        if (context == null) return;

        if (!CoreUtils.checkNetActive(context)) {
            //网络不可用
            notifyNetError(ResponseHeader.create(StatusCode.NET_NONE, "网络不可用，请检查您的网络链接"));
            return;
        }

        Response<JsonObject> response;
        try {
            //when canceled ,it throws IOException("canceled")
            response = mCall.execute();

            //网络数据返回调用者
            HttpStatusLine statusLine = new HttpStatusLine(response.code(), response.message());
            Pair<JsonObject, JsonElement> pair = parseHeaderContent(response.body());
            dispatchRetrofitResponse(statusLine, pair.first, pair.second);

            //Log 网络返回数据
        } catch (Exception e) {
            e.printStackTrace();

            if (TextUtils.equals("canceled", e.getMessage())) {//请求被取消
                notifyNetCanceled();
                NetLogger.d(NetLogger.TAG, "请求执行中被取消");
            } else {
                //请求执行过程中，发生异常
                notifyNetError(ResponseHeader.create(StatusCode.NET_ERROR, "未知的网络错误"));
            }
        }
    }

    @NonNull
    private Pair<JsonObject, JsonElement> parseHeaderContent(@Nullable JsonObject resp) {
        if (resp == null) return Pair.create(null, null);

        //通用请求处理，不带h，c的情况
        boolean hasHeader = resp.has(ResponseSchema.header);
        if (!hasHeader) {
            JsonObject header = new JsonObject();
            header.addProperty(HeaderSchema.code, StatusCode.SERVER_SUCCESS);

            return Pair.create(header, (JsonElement) resp);
        }

        JsonElement elementHeader = resp.get(ResponseSchema.header);
        JsonElement elementContent = resp.get(ResponseSchema.content);

        JsonObject header = (elementHeader instanceof JsonObject) ? (JsonObject) elementHeader : null;

        return Pair.create(header, elementContent);
    }

    private void dispatchRetrofitResponse(@NonNull HttpStatusLine statusLine, @Nullable JsonObject header, @Nullable JsonElement content) {

        if (header == null) header = new JsonObject();//返回客户端安全类型
        if (content == null) content = new JsonObject();

        if (statusLine.getCode() == HTTP_OK) {
            notifyNetSuccess(header, content);//网络数据返回，认为请求已经执行完成
        } else {
            notifyNetError(ResponseHeader.create(StatusCode.NET_ERROR, statusLine.getMessage()));
        }
    }

    protected abstract void notifyNetSuccess(@NonNull JsonObject header, @NonNull JsonElement content);

    protected abstract void notifyNetError(@NonNull ResponseHeader rh);

    protected abstract void notifyNetCanceled();

}
