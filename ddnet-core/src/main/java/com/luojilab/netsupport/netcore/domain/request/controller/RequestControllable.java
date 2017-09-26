package com.luojilab.netsupport.netcore.domain.request.controller;

import android.support.annotation.NonNull;

import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.netcore.domain.RequestRespondable;

/**
 * Created by liushuo on 16/3/27.
 */
public interface RequestControllable extends RequestRespondable {

    void enqueueRequest(@NonNull Request request);

    void cancelRequest();

    boolean isManagedRequest(@NonNull Request request);

}
