package com.luojilab.netsupport.netcore.domain.eventbus;

import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventPreNetRequest {
    public final Request mRequest;

    public EventPreNetRequest(Request request) {
        mRequest = request;
    }

}
