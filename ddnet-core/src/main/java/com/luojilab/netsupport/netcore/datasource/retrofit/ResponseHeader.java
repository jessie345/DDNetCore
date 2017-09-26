package com.luojilab.netsupport.netcore.datasource.retrofit;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/20 0020.
 */
public class ResponseHeader implements Serializable {

    public ResponseHeader(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public static ResponseHeader create(int code, String message) {
        return new ResponseHeader(code, message);
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
