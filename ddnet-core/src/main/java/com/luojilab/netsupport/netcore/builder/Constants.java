package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liushuo on 2017/5/27.
 */

public class Constants {
    //http 请求方法
    public static final int HTTP_METHOD_POST = 0;
    public static final int HTTP_METHOD_GET = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HTTP_METHOD_POST, HTTP_METHOD_GET})
    public @interface HttpMethod {
    }

    //数据请求执行模式
    public static final int STRATEGY_NONE = -1;//未指定请求策略
    public static final int STRATEGY_ONLY_NET = 0;//直接请求网络
    public static final int STRATEGY_REFRESH_CACHE = 1;//优先返回cache数据，并请求网络刷新cache,返回最新数据
    public static final int STRATEGY_LEVEL3_CACHE = 2;//网络请求走三级缓存

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STRATEGY_NONE, STRATEGY_ONLY_NET, STRATEGY_REFRESH_CACHE, STRATEGY_LEVEL3_CACHE})
    public @interface RequestStrategy {
    }

    //数据请求的Content—Type
    public static final int CONTENT_TYPE_JSON = 0;// application/json
    public static final int CONTENT_TYPE_FORM = 1;// application/x-www-form-urlencoded

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONTENT_TYPE_JSON, CONTENT_TYPE_FORM})
    public @interface ContentType {
    }

}
