package com.luojilab.netsupport.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuo on 2017/8/23.
 */

public class NetCoreInitializer {
    private static NetCoreInitializer sNetSupportInitializer;

    private Context mAppContext;
    private String mBaseUrl;
    private List<IBuilderDecorator> mOkHttpBuilderDecorators = new ArrayList<>(0);

    private NetCoreInitializer() {
    }

    public static NetCoreInitializer getInstance() {
        if (sNetSupportInitializer == null) {
            synchronized (NetCoreInitializer.class) {
                if (sNetSupportInitializer == null) {
                    sNetSupportInitializer = new NetCoreInitializer();
                }
            }
        }

        return sNetSupportInitializer;
    }

    public NetCoreInitializer baseUrl(@NonNull String baseUrl) {
        Preconditions.checkNotNull(baseUrl);

        mBaseUrl = baseUrl;
        return this;
    }

    public NetCoreInitializer appContext(@NonNull Context context) {
        Preconditions.checkNotNull(context);

        mAppContext = context;
        return this;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void addOkHttpClientBuilderDecorator(@NonNull IBuilderDecorator decorator) {
        Preconditions.checkNotNull(decorator);

        mOkHttpBuilderDecorators.add(decorator);
    }

    @NonNull
    public List<IBuilderDecorator> getOkHttpBuilderDecorators() {
        return mOkHttpBuilderDecorators;
    }

}
