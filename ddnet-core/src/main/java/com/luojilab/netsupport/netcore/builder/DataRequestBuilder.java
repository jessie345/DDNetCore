package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

/**
 * Created by liushuo on 2017/5/26.
 */

public class DataRequestBuilder {

    /**
     * 配置对象请求实例
     *
     * @param apiPath
     * @return
     */
    public static ObjectRequestBuilder asObjectRequest(@NonNull String apiPath) {
        Preconditions.checkNotNull(apiPath);

        return new ObjectRequestBuilder(apiPath);
    }

    /**
     * 配置数组请求实例
     *
     * @param apiPath
     * @return
     */
    public static ArrayRequestBuilder asArrayRequest(@NonNull String apiPath) {
        Preconditions.checkNotNull(apiPath);

        return new ArrayRequestBuilder(apiPath);
    }

}
