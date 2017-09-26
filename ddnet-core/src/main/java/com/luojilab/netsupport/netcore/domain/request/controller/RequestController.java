package com.luojilab.netsupport.netcore.domain.request.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.netcore.domain.RequestManager;
import com.luojilab.netsupport.netcore.domain.eventbus.EventRequestCanceled;
import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventPreNetRequest;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liushuo on 16/3/27.
 */
public class RequestController implements RequestControllable {
    private Set<String> mRequestIds = new HashSet<>();

    public RequestController() {
    }

    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        //do nothing
    }

    @Override
    public void onNetRequestError(EventNetError error) {
        removeRequestId(error.mRequest.getRequestId());
    }

    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        removeRequestId(cancel.mRequest.getRequestId());
    }

    private void removeRequestId(@Nullable String requestId) {
        if (!TextUtils.isEmpty(requestId)) {
            mRequestIds.remove(requestId);
        }
    }

    @Override
    public void enqueueRequest(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        //首先执行同步操作
        String requestId = request.getRequestId();
        if (!TextUtils.isEmpty(requestId)) {
            mRequestIds.add(request.getRequestId());
        }

        request.attachRequestController(this);

        //最后执行将任务添加到任务队列
        RequestManager.getInstance().enqueueRequest(request);
    }

    @Override
    public void cancelRequest() {
        Iterator<String> itr = mRequestIds.iterator();
        while (itr.hasNext()) {
            String requestId = itr.next();
            if (TextUtils.isEmpty(requestId)) continue;

            RequestManager.getInstance().cancelRequest(requestId);
            itr.remove();
        }
    }

    @Override
    public void onReceiveResponse(EventResponse event) {
        if (event.mRequest.isDone()) {
            removeRequestId(event.mRequest.getRequestId());
        }
    }

    @Override
    public boolean isManagedRequest(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        return request.getRequestController() == this;
    }
}
