package com.luojilab.netsupport.netcore.domain;

import android.os.Process;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.utils.NetLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liushuo on 16/3/21.
 * 防止并发访问发生错误 submitRequest,cancelRequest,afterExecute 可能处于不同的线程
 */
public class RequestExecutor extends ThreadPoolExecutor {

    private static final int POOLSIZE = 3;

    Map<String, List<Future<String>>> mFutures = new HashMap<>();
    Map<Future, Request> mRequests = new HashMap<>();

    RequestExecutor() {
        super(POOLSIZE, POOLSIZE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new MyThreadFactory());
    }

    /**
     * 客户端使用本方法提交一个绑定到指定requestId的任务，不同情况下可以根据requestId取消任务
     *
     * @param requestId
     * @param task
     * @return
     */
    public synchronized void submitRequest(@NonNull String requestId, @NonNull RequestManager.RequestRunnable task) {
        Preconditions.checkNotNull(task);
        Preconditions.checkArgument(!TextUtils.isEmpty(requestId), "试图提交请求id为空的请求执行，不执行");

        //执行同步操作
        RunnableFuture<String> future = newTaskFor(task, requestId);

        List<Future<String>> futures = mFutures.get(requestId);
        if (futures == null) {
            futures = new ArrayList<>();
            mFutures.put(requestId, futures);
        }
        futures.add(future);
        mRequests.put(future, task.getRequest());

        //执行异步任务
        execute(future);


    }

    /**
     * 取消正在执行的或者正在排队的请求
     *
     * @param requestId
     */
    public synchronized void cancelRequest(@NonNull String requestId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(requestId), "试图取消的request id非法,requestId:" + requestId);

        List<Future<String>> list = mFutures.get(requestId);
        if (list != null && list.size() > 0) {
            for (Future<String> future : list) {

                Request request = mRequests.remove(future);
                if (request != null) {
                    request.setCanceled(true);

                    if (request.getCall() != null) {
                        request.getCall().cancel();
                    }
                }

                if (future.isDone() || future.isCancelled()) continue;
                future.cancel(true);
                NetLogger.d(NetLogger.TAG, "请求在等待执行时被取消" + requestId);

            }
            list.clear();
        }

    }

    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (!(r instanceof Future)) return;

        try {
            Future<String> f = (Future<String>) r;
            Request request = mRequests.get(f);
            mRequests.remove(f);
            if (request == null) return;

            String requestId = request.getRequestId();
            if (TextUtils.isEmpty(requestId)) return;

            List<Future<String>> futures = mFutures.get(requestId);
            if (futures != null && futures.size() > 0) {
                futures.remove(r);
            }

        } catch (Exception e) {
            NetLogger.e(e, null);
        }
    }

    static class MyThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Process.THREAD_PRIORITY_BACKGROUND)
                t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return t;
        }
    }
}
