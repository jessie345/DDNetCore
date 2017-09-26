package com.luojilab.netsupport.netcore.domain.eventbus;

import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventNetError {
    public ResponseHeader mRB;
    public Request mRequest;

    public EventNetError(Request request, ResponseHeader rb) {
        this.mRB = rb;
        this.mRequest = request;
    }

}
