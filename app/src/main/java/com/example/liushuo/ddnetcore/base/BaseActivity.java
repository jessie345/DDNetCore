package com.example.liushuo.ddnetcore.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.RequestManager;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.netcore.network.NetworkControl;
import com.luojilab.netsupport.netcore.network.NetworkControlListener;

public abstract class BaseActivity extends AppCompatActivity implements NetworkControlListener {

    private NetworkControl mNetworkControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkControl = NetworkControl.create();
    }


    @Override
    protected void onStart() {
        mNetworkControl.register();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mNetworkControl.unRegister();
        mNetworkControl.cancelRequest();
        super.onStop();
    }

    public void enqueueRequest(Request request) {
        if (!mNetworkControl.isControlListenerRegistered(this)) {
            mNetworkControl.registerControlListener(this);
        }

        mNetworkControl.enqueueRequest(request);
    }

    public void cancelRequest(@NonNull String reqeustId) {
        Preconditions.checkNotNull(reqeustId);

        RequestManager.getInstance().cancelRequest(reqeustId);
    }

    @Override
    public void handlePreNetRequest(@NonNull Request request) {
    }

    @Override
    public void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb) {
    }

    @Override
    public void handleReceivedResponse(@NonNull EventResponse event) {
    }
}
