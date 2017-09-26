package com.luojilab.netsupport.netcore.datasource.retrofit;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.luojilab.netsupport.utils.NetCoreInitializer;

/**
 * Created by liushuo on 2017/3/7.
 */

public class HeaderHelpers {

    /**
     * 返回客户端一个请求参数的容器，request中只需要将请求参数addProperty到容器中即可
     *
     * @return
     */
    @NonNull
    public static JsonObject getParamsContainer() {
        JsonObject root = new JsonObject();
        return root;
    }

}
