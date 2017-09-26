package com.luojilab.netsupport.netcore.network;

import android.support.annotation.NonNull;

import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liujun on 17/3/9.
 */

public interface NetworkControlListener {

    void handlePreNetRequest(@NonNull Request request);

    void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb);

    void handleReceivedResponse(@NonNull EventResponse event);
}
