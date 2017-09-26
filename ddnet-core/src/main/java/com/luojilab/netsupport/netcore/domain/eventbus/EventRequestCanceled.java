package com.luojilab.netsupport.netcore.domain.eventbus;

import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventRequestCanceled {
    public Request mRequest;

    public EventRequestCanceled(Request request) {
        this.mRequest = request;
    }

}
