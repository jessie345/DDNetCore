package com.luojilab.netsupport.netcore.datasource.retrofit;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.utils.ClientBuilderUtils;
import com.luojilab.netsupport.utils.NetCoreInitializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liushuo on 2017/3/2.
 */

public class RetrofitClient {
    private static Retrofit sRetrofit;

    private static Map<Class, Object> sServiceCache = new HashMap<>();

    private RetrofitClient() {
    }

    @NonNull
    public static <T> T getApiService(@NonNull Class<T> cls) {
        Preconditions.checkNotNull(cls);

        synchronized (RetrofitClient.class) {
            if (sRetrofit == null) {

                OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .retryOnConnectionFailure(true)
//                        .dns()  // 可用于配置http dns
//                        .socketFactory(HttpDns.getSocketFactory())
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();
                                HttpUrl originalHttpUrl = original.url();

                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Content-Type", "application/json")
                                        .url(originalHttpUrl);

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }).addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = configureCommonHeaders(chain);
                                return chain.proceed(request);
                            }
                        });

                ClientBuilderUtils.decorateClientBuilder(okHttpClientBuilder);
                OkHttpClient okHttpClient = okHttpClientBuilder.build();

                sRetrofit = new Retrofit.Builder()
                        .baseUrl(NetCoreInitializer.getInstance().getBaseUrl())
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }

        synchronized (sServiceCache) {
            if (sServiceCache.get(cls) == null) {
                sServiceCache.put(cls, sRetrofit.create(cls));
            }
        }

        return (T) sServiceCache.get(cls);
    }

    @NonNull
    private static Request configureCommonHeaders(@NonNull Interceptor.Chain chain) {
        Request request = chain.request()
                .newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        return request;
    }
}


