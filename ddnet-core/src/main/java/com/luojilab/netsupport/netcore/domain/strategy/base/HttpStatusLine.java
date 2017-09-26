package com.luojilab.netsupport.netcore.domain.strategy.base;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/20 0020.
 */
class HttpStatusLine implements Serializable {

    public HttpStatusLine(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public static HttpStatusLine create(int code, String message) {
        return new HttpStatusLine(code, message);
    }

    int mCode;
    String mMessage;

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
