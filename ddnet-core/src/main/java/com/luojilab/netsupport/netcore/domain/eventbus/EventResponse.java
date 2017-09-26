package com.luojilab.netsupport.netcore.domain.eventbus;

import com.luojilab.netsupport.netcore.domain.DataFrom;
import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liushuo on 16/3/27.
 */
public class EventResponse {
    public final Request mRequest;
    public final DataFrom mDataFrom;

    public EventResponse(Request request, DataFrom from) {
        mRequest = request;
        mDataFrom = from;
    }
}
