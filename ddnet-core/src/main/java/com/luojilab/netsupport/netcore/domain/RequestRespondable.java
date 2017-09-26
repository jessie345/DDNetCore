package com.luojilab.netsupport.netcore.domain;

import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventPreNetRequest;
import com.luojilab.netsupport.netcore.domain.eventbus.EventRequestCanceled;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;

/**
 * Created by liushuo on 16/4/13.
 * 具有请求响应能力的实体可以响应如下事件:
 * 1.即将发生网络事务
 * 2.请求执行过程中发生错误（http错误或者服务器接口调用异常）
 * 3.请求被取消
 * 4.网络请求返回结果
 */
public interface RequestRespondable {

    /**
     * 请求即将发起网络事务
     *
     * @param event
     */
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onPreNetRequest(EventPreNetRequest event);

    /**
     * 请求发生错误（执行网络事务阶段）
     *
     * @param error
     */
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onNetRequestError(EventNetError error);//remove TAG

    /**
     * 请求被取消（执行获取本地数据或者执行网络事务阶段）
     *
     * @param cancel
     */
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onRequestCanceled(EventRequestCanceled cancel);//remove TAG

    /**
     * 请求返回结果（本地数据或者网络数据）
     *
     * @param event
     */
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onReceiveResponse(EventResponse event); //remove TAG if over


}
