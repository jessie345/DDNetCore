package com.luojilab.netsupport.netcore.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;

import java.lang.ref.WeakReference;

/**
 * Created by liujun on 17/3/9.
 */

public class NetworkControl extends RequestUIInterface {

    /**
     * 弱引用，当执行gc时，ref会被回收，这样可能会带来问题，需要观察
     */
    private WeakReference<NetworkControlListener> mListenerRef;

    private NetworkControl() {
        init();
    }

    public static NetworkControl create() {
        return new NetworkControl();
    }

    public void registerControlListener(@NonNull NetworkControlListener listener) {
        Preconditions.checkNotNull(listener);

        mListenerRef = new WeakReference(listener);
    }

    public boolean isControlListenerRegistered(@NonNull NetworkControlListener listener) {
        Preconditions.checkNotNull(listener);

        return mListenerRef != null && mListenerRef.get() == listener;
    }

    @Nullable
    private NetworkControlListener getControlListener() {
        if (mListenerRef == null) return null;

        return mListenerRef.get();
    }

    @Override
    protected void handlePreNetRequest(@NonNull Request request) {
        NetworkControlListener listener = getControlListener();
        if (listener == null) return;

        listener.handlePreNetRequest(request);
    }

    @Override
    protected void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb) {
        NetworkControlListener listener = getControlListener();
        if (listener == null) return;

        listener.handleNetRequestError(request, rb);
    }

    @Override
    protected void handleReceivedResponse(@NonNull EventResponse event) {
        NetworkControlListener listener = getControlListener();
        if (listener == null) return;

        listener.handleReceivedResponse(event);
    }
}
