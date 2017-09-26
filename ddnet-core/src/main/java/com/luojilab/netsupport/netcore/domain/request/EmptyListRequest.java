package com.luojilab.netsupport.netcore.domain.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luojilab.netsupport.netcore.domain.DataFrom;
import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by liushuo on 2017/8/7.
 */

public class EmptyListRequest extends Request<List<Object>> {

    private boolean mRrequestError;
    private boolean isLoadMore;

    /**
     * 如果k表示Bean，则dataClass 与k为同一个类型
     * 如何k 表示List<Bean>,则dataClass 与Bean为同一个类型
     */
    public EmptyListRequest(boolean requestError) {
        super(Object.class);
        mRrequestError = requestError;
    }

    @NonNull
    @Override
    public Call<JsonObject> getCall() {
        return null;
    }

    @Nullable
    @Override
    protected JsonElement adaptStructForCache(@NonNull JsonElement v) {
        return null;
    }

    @Override
    protected void cache2Memory(@NonNull List<Object> data) {

    }

    @NonNull
    @Override
    protected TypeToken<List<Object>> getTypeToken() {
        return null;
    }

    @Nullable
    @Override
    public List<Object> getResult() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new Object());
        }
        return list;
    }

    @Override
    public void perform() {
        super.perform();

        preNetRequest();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object event = mRrequestError ?
                new EventNetError(this, null) :
                new EventResponse(this, DataFrom.NET);

        EventBus.getDefault().post(event);

    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
    }
}
