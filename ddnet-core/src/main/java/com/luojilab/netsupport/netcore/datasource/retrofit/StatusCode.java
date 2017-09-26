package com.luojilab.netsupport.netcore.datasource.retrofit;

/**
 * Created by liushuo on 2017/3/3.
 */

public interface StatusCode {
    int NET_NONE = 800;//网络不可用
    int NET_ERROR = 900;// 网络错误

    /*网络ok,常用服务的错误码*/
    int SERVER_SUCCESS = 0;                      //网络请求成功
}
