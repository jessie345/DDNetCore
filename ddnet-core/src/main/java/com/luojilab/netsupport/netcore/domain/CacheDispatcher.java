package com.luojilab.netsupport.netcore.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;
import com.luojilab.netsupport.netcore.datasource.DALFactory;
import com.luojilab.netsupport.utils.NetLogger;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by liushuo on 16/3/20.
 */
public class CacheDispatcher implements Handler.Callback {
    public static final String TAG = "CacheDispatcher";

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private BaseLocalStorage mLocalStorage;

    private static CacheDispatcher mInstance;

    private CacheDispatcher() {
        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), this);
        mLocalStorage = DALFactory.getBaseStorage();
    }

    @NonNull
    public static CacheDispatcher getInstance() {
        synchronized (CacheDispatcher.class) {
            if (mInstance == null) {
                mInstance = new CacheDispatcher();
            }
        }
        return mInstance;
    }

    public void dispatchDataCache(@NonNull Class dataClass, @NonNull JsonElement data) {
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(data);

        if (data.isJsonNull()) {
            logError(dataClass, data);
            return;
        }

        if (!data.isJsonObject() && !data.isJsonArray()) {
            logError(dataClass, data);
            return;
        }

        Message msg = mHandler.obtainMessage();

        List<JsonObject> list = CoreUtils.toJsonObjects(data);
        msg.obj = new CacheElement(dataClass.getSimpleName(), list.toArray(new JsonObject[list.size()]));
        mHandler.sendMessage(msg);
    }

    private void logError(Class dataClass, JsonElement data) {

        NetLogger.d(NetLogger.TAG, "无法存储数据 baselocalstorage:" + mLocalStorage + ",type:" + String.valueOf(dataClass) + ",data:" + data);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj != null && msg.obj instanceof CacheElement) {
            CacheElement ce = (CacheElement) msg.obj;
            //1.数据缓存本地
            mLocalStorage.saveData(ce.mDataType, ce.mArray);
        }
        return true;
    }

    public static class CacheElement {
        public final JsonObject[] mArray;
        public final String mDataType;

        public CacheElement(String dataType, JsonObject... array) {
            this.mArray = array;
            this.mDataType = dataType;
        }
    }
}
