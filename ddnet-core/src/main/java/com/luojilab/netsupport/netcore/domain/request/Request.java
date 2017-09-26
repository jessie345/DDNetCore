package com.luojilab.netsupport.netcore.domain.request;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.luojilab.netsupport.utils.NetLogger;
import com.luojilab.netsupport.netcore.datasource.retrofit.HeaderHelpers;
import com.luojilab.netsupport.netcore.datasource.retrofit.HeaderSchema;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.datasource.retrofit.StatusCode;
import com.luojilab.netsupport.netcore.domain.CacheDispatcher;
import com.luojilab.netsupport.netcore.domain.DataFrom;
import com.luojilab.netsupport.netcore.domain.ResponseListener;
import com.luojilab.netsupport.netcore.domain.eventbus.EventNetError;
import com.luojilab.netsupport.netcore.domain.eventbus.EventPreNetRequest;
import com.luojilab.netsupport.netcore.domain.eventbus.EventRequestCanceled;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.controller.RequestControllable;
import com.luojilab.netsupport.netcore.manager.PreferenceManager;
import com.luojilab.netsupport.netcore.utils.CoreUtils;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;

/**
 * Created by liushuo on 16/3/17.
 */
public abstract class Request<K> implements ResponseListener<K> {
    public static final long RESPONSE_VALID_THRESHOLD = 30 * 60 * 1000;

    public enum State {IDLE, RUNNING, DONE}

    protected Class mDataClass;
    /*请求的唯一标识,对等于get 请求中的请求路径，post请求中的路径+参数*/
    private String mRequestId;

    protected boolean isCanceled;
    private RequestControllable mRequestController;

    private volatile K mResult;
    private volatile State mState = State.IDLE;

    private long mRespExpire = RESPONSE_VALID_THRESHOLD;

    protected final JsonObject mParamsContainer = HeaderHelpers.getParamsContainer();

    /**
     * 如果k表示Bean，则dataClass 与k为同一个类型
     * 如何k 表示List<Bean>,则dataClass 与Bean为同一个类型
     *
     * @param dataClass
     */
    public Request(Class dataClass) {
        this.mDataClass = dataClass;

        mRequestId = getClass().getSimpleName();
    }

    protected void addNetParameter(@NonNull String key, @NonNull Boolean value) {
        mParamsContainer.addProperty(key, value);
    }

    protected void addNetParameter(@NonNull String key, @NonNull Character value) {
        mParamsContainer.addProperty(key, value);
    }

    protected void addNetParameter(@NonNull String key, @NonNull Number value) {
        mParamsContainer.addProperty(key, value);
    }

    protected void addNetParameter(@NonNull String key, @NonNull String value) {
        mParamsContainer.addProperty(key, value);
    }

    @NonNull
    public abstract Call<JsonObject> getCall();

    /**
     * 子类必须调用父类方法，初始化请求状态
     */
    @CallSuper
    public void perform() {
        if (mState != State.IDLE) throw new IllegalStateException("请求无法重复添加");

        //初始化请求的默认状态
        setCanceled(false);

        mState = State.RUNNING;
    }

    /**
     * 将服务器返回的数据格式 转换适配到可以进行缓存的数据结构
     *
     * @param v
     * @return
     */
    @Nullable
    protected abstract JsonElement adaptStructForCache(@NonNull JsonElement v);

    protected void cache2DB(@NonNull JsonElement data) {
        CacheDispatcher.getInstance().dispatchDataCache(mDataClass, data);

        NetLogger.d(NetLogger.TAG, "请求结果缓存到数据库:" + data.toString());
    }

    protected abstract void cache2Memory(@NonNull K data);

    @NonNull
    protected abstract TypeToken<K> getTypeToken();


    @Nullable
    public K getResult() {
        return mResult;
    }

    protected void setDone(boolean isDone) {
        mState = isDone ? State.DONE : State.RUNNING;
    }

    public boolean isRunning() {
        return mState == State.RUNNING;
    }

    public boolean isDone() {
        return mState == State.DONE;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    /**
     * requestId的默认值为类名
     *
     * @return
     */
    @NonNull
    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(@NonNull String mRequestId) {
        if (TextUtils.isEmpty(mRequestId))
            throw new IllegalArgumentException("不能将请求id设置为空值,有任何疑问，联系作者");

        this.mRequestId = mRequestId;
    }


    /**
     * 判定请求过期的策略由父亲类决定，子类提供requestId标示不同的请求，为空则每次都过期
     *
     * @return
     */
    public final boolean isResponseValid() {
        if (TextUtils.isEmpty(mRequestId)) return false;

        long lastRequestTime = PreferenceManager.getLongValue(mRequestId);
        long currentTime = System.currentTimeMillis();
        boolean valid = currentTime - lastRequestTime < getRespExpire();

        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 网络请求结果有效性:" + valid);

        return valid;
    }

    /**
     * 子类如果有不同的过期时间，需要重写
     *
     * @return
     */
    public long getRespExpire() {
        return mRespExpire;
    }

    public void setRespExpire(long threshold) {
        mRespExpire = threshold;
    }

    public void attachRequestController(@Nullable RequestControllable requestController) {
        this.mRequestController = requestController;
    }

    @Nullable
    public RequestControllable getRequestController() {
        return mRequestController;
    }

    @CallSuper
    @Override
    public void onRetrofitResponse(@NonNull JsonObject header, @NonNull JsonElement content) {
        if (isCanceled()) {
            EventBus.getDefault().post(new EventRequestCanceled(this));
            NetLogger.d(NetLogger.TAG, "请求被取消：" + mDataClass.getSimpleName());

            return;
        }

        extendNetResponseValid();
        JsonElement element = adaptStructForCache(content);

        if (element != null && !element.isJsonNull() && mDataClass != null) {
            cache2DB(element);
            mResult = CoreUtils.json2Bean(element, getTypeToken());

            //返回结果不为null，才执行缓存到内存操作
            if (mResult != null) {
                cache2Memory(mResult);

                NetLogger.d(NetLogger.TAG, "请求结果缓存到内存:" + content);

            }
        }
        setDone(true);

        //通知ui网络返回
        dispatchRetrofitResponse(header);

    }

    @CallSuper
    @Override
    public void onCacheResponse(@NonNull K k, boolean isDone) {
        if (isCanceled()) {
            EventBus.getDefault().post(new EventRequestCanceled(this));
            NetLogger.d(NetLogger.TAG, "请求被取消：" + mDataClass.getSimpleName());

            return;
        }

        mResult = k;
        setDone(isDone);

        //通知ui缓存数据返回
        EventBus.getDefault().post(new EventResponse(this, DataFrom.CACHE));

        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 返回缓存数据:" + mResult);

    }

    @Override
    public void preNetRequest() {
        EventBus.getDefault().post(new EventPreNetRequest(this));
        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 即将执行网络请求");
    }

    @Override
    public void onNetError(@NonNull ResponseHeader rh) {
        EventBus.getDefault().post(new EventNetError(this, rh));
        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 网络发生错误");
    }

    @Override
    public void onNetCanceled() {
        EventBus.getDefault().post(new EventRequestCanceled(this));
        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 请求被取消");
    }

    protected void dispatchRetrofitResponse(@NonNull JsonObject header) {
        NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 网络数据返回:" + mResult);

        JsonPrimitive jp = header.getAsJsonPrimitive(HeaderSchema.code);
        if (jp == null) {
            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(-1, "header code is null")));
            return;
        }

        int code = jp.getAsInt();
        if (code == StatusCode.SERVER_SUCCESS) {
            EventBus.getDefault().post(new EventResponse(this, DataFrom.NET));
        } else {
            String message = "";

            JsonPrimitive msgJP = header.getAsJsonPrimitive(HeaderSchema.error_msg);
            if (msgJP != null) {
                message = msgJP.getAsString();
            }

            EventBus.getDefault().post(new EventNetError(this, ResponseHeader.create(code, message)));
        }
    }

    private void extendNetResponseValid() {
        if (!TextUtils.isEmpty(mRequestId)) {
            PreferenceManager.putLong(mRequestId, System.currentTimeMillis());

            NetLogger.d(NetLogger.TAG, getClass().getSimpleName() + " 延长响应有效时间");

        }
    }

}
