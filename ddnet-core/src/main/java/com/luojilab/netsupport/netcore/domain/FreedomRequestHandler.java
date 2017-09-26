package com.luojilab.netsupport.netcore.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.eventbus.EventRequestCanceled;
import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventPreNetRequest;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liushuo on 16/4/13.
 * Freedom Request is that has no RequestController control it
 */
public class FreedomRequestHandler implements RequestRespondable {

    private Map<String, RequestRespondable> mFreedomRequestHandler = new HashMap<>();

    public boolean isFreedomRequest(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        return request.getRequestController() == null;
    }


    public void registerFreedomRequestHandler(@NonNull String requestId, @NonNull RequestRespondable handler) {
        Preconditions.checkNotNull(handler);
        Preconditions.checkArgument(!TextUtils.isEmpty(requestId));

        mFreedomRequestHandler.put(requestId, handler);
    }

    @Nullable
    private RequestRespondable getFreedomRequestHandler(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        String requestId = request.getRequestId();
        if (TextUtils.isEmpty(requestId))
            return null;

        return mFreedomRequestHandler.get(requestId);
    }


    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        RequestRespondable handler = getFreedomRequestHandler(event.mRequest);
        if (handler == null) return;

        handler.onPreNetRequest(event);
    }

    @Override
    public void onNetRequestError(EventNetError error) {
        RequestRespondable handler = getFreedomRequestHandler(error.mRequest);
        if (handler == null) return;

        handler.onNetRequestError(error);
    }

    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        RequestRespondable handler = getFreedomRequestHandler(cancel.mRequest);
        if (handler == null) return;

        handler.onRequestCanceled(cancel);
    }

    @Override
    public void onReceiveResponse(EventResponse event) {
        RequestRespondable handler = getFreedomRequestHandler(event.mRequest);
        if (handler == null) return;

        handler.onReceiveResponse(event);
    }
}
