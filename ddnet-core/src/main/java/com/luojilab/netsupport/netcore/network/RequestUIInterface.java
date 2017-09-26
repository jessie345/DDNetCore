package com.luojilab.netsupport.netcore.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.FreedomRequestHandler;
import com.luojilab.netsupport.netcore.domain.RequestRespondable;
import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventPreNetRequest;
import com.luojilab.netsupport.netcore.domain.eventbus.EventRequestCanceled;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.netcore.domain.request.controller.RequestControllable;
import com.luojilab.netsupport.netcore.domain.request.controller.RequestController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author: liushuo
 * Date: 2016/4/7
 */
public abstract class RequestUIInterface implements RequestControllable {

    private RequestController mController;
    private FreedomRequestHandler mFreedomRequestHandler;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    protected void init() {
        mController = new RequestController();
        mFreedomRequestHandler = new FreedomRequestHandler();
    }

    public boolean isRegistered() {
        return EventBus.getDefault().isRegistered(this);
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onPreNetRequest(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onPreNetRequest(event);
        handlePreNetRequest(event.mRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onNetRequestError(EventNetError error) {
        if (mFreedomRequestHandler.isFreedomRequest(error.mRequest)) {
            mFreedomRequestHandler.onNetRequestError(error);
            return;
        }
        if (!isManagedRequest(error.mRequest)) return;

        mController.onNetRequestError(error);
        handleNetRequestError(error.mRequest, error.mRB);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onRequestCanceled(EventRequestCanceled cancel) {
        if (mFreedomRequestHandler.isFreedomRequest(cancel.mRequest)) {
            mFreedomRequestHandler.onRequestCanceled(cancel);
            return;
        }

        if (!isManagedRequest(cancel.mRequest)) return;

        mController.onRequestCanceled(cancel);
        handleRequestCanceled(cancel.mRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onReceiveResponse(EventResponse event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onReceiveResponse(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onReceiveResponse(event);
        handleReceivedResponse(event);
    }


    @Override
    public void enqueueRequest(@NonNull final Request request) {
        Preconditions.checkNotNull(request);

        if (isRegistered() || Looper.getMainLooper() != Looper.myLooper()) {
            mController.enqueueRequest(request);
            return;
        }

        //主线程中的enqueue操作，放到主线程的消息循环中，防止preNetRequest()的回调在注册事件总线之前执行(onCreate()中
        // 发送请求，有时候接收不到回调)
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mController.enqueueRequest(request);
            }
        });
    }

    @Override
    public void cancelRequest() {
        mController.cancelRequest();
    }

    @Override
    public boolean isManagedRequest(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        return mController.isManagedRequest(request);
    }

    public void registerFreedomRequestHandler(@NonNull String requestId, @NonNull RequestRespondable handler) {
        Preconditions.checkNotNull(requestId);
        Preconditions.checkNotNull(handler);

        mFreedomRequestHandler.registerFreedomRequestHandler(requestId, handler);
    }

    protected abstract void handlePreNetRequest(@NonNull Request request);

    protected abstract void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb);

    //多数情况下 用户可能不关心请求是否被取消，有特殊需求的页面，可以重写该方法
    protected void handleRequestCanceled(@NonNull Request request) {
    }

    protected abstract void handleReceivedResponse(@NonNull EventResponse event);
}
