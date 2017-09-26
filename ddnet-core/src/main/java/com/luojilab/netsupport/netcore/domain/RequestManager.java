package com.luojilab.netsupport.netcore.domain;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.utils.NetLogger;

import java.util.concurrent.Executor;

/**
 * Created by liushuo on 16/3/17.
 */
public class RequestManager {
    private RequestExecutor mRequestExecutor;

    private static RequestManager sInstance;

    private RequestManager() {
        mRequestExecutor = new RequestExecutor();
    }

    @NonNull
    public static RequestManager getInstance() {
        synchronized (RequestManager.class) {
            if (sInstance == null) {
                sInstance = new RequestManager();
            }
        }
        return sInstance;
    }

    public synchronized void enqueueRequest(@NonNull Request request) {
        Preconditions.checkNotNull(request);

        RequestRunnable runnable = new RequestRunnable(request);
        mRequestExecutor.submitRequest(request.getRequestId(), runnable);

        NetLogger.d(NetLogger.TAG, "添加请求 " + request.getRequestId() + ",当前活跃的任务数量为 " + mRequestExecutor.getActiveCount());
    }

    /**
     * request 在自定义线程池中运行，不能被取消
     *
     * @param executor
     * @param request
     */
    public void enqueueRequest(@NonNull Executor executor, @NonNull Request request) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(request);

        Runnable runnable = new RequestRunnable(request);
        executor.execute(runnable);

    }


    /**
     * 取消正在执行的或者正在排队的请求
     *
     * @param requestId
     */
    public void cancelRequest(@NonNull String requestId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(requestId));

        mRequestExecutor.cancelRequest(requestId);
    }


    class RequestRunnable implements Runnable {
        Request request;

        public RequestRunnable(Request request) {
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        @Override
        public void run() {
            request.perform();
        }
    }

}
