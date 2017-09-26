package com.luojilab.netsupport.utils;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by liushuo on 2017/8/24.
 */

public class ClientBuilderUtils {
    public static void decorateClientBuilder(@NonNull OkHttpClient.Builder builder) {
        Preconditions.checkNotNull(builder);

        List<IBuilderDecorator> decorators = NetCoreInitializer.getInstance().
                getOkHttpBuilderDecorators();
        for (IBuilderDecorator decorator : decorators) {
            decorator.apply(builder);
        }
    }
}
