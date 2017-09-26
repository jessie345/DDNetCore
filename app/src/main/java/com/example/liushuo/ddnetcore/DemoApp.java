package com.example.liushuo.ddnetcore;

import android.app.Application;

import com.google.gson.JsonObject;
import com.luojilab.netsupport.utils.NetCoreInitializer;

/**
 * Created by liushuo on 2017/9/22.
 */

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NetCoreInitializer.getInstance()
                .appContext(getApplicationContext())
                .baseUrl("http://127.0.0.1:3000");

    }
}
