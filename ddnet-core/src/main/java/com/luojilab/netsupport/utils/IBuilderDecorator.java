package com.luojilab.netsupport.utils;

import okhttp3.OkHttpClient;

/**
 * Created by liushuo on 2017/8/24.
 */

public interface IBuilderDecorator {
    void apply(OkHttpClient.Builder builder);
}
