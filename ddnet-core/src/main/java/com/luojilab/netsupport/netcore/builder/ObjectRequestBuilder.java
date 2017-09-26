package com.luojilab.netsupport.netcore.builder;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.utils.NetCoreInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.luojilab.netsupport.netcore.builder.Constants.CONTENT_TYPE_JSON;
import static com.luojilab.netsupport.netcore.builder.Constants.HTTP_METHOD_POST;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_ONLY_NET;
import static com.luojilab.netsupport.netcore.builder.Constants.STRATEGY_NONE;

public class ObjectRequestBuilder {

    /*参数无默认值，调用者必须传参，否则异常*/
    private String mApiPath;//请求路径
    private Class mDataClass;//对象的类型

    /*参数有默认值，调用者可以不传递参数*/
    private String mApiDomain;//请求路径

    private String mCacheId;//缓存id，如果为空，则 mMemoryCache=false,mDBCache=false
    private boolean mMemoryCache;//是否缓存内存,默认false
    private boolean mDBCache;//是否缓存数据库,默认false

    private long mRespExpire;//响应结果过期时间

    /*参数无默认值，调用者可以不传递参数*/
    private String mRequestId;//请求id，默认为类名,用于取消请求，请求区分，请求过期时间保存

    private List<String> mSegments = new ArrayList<>();//c中内容的解析字段
    private Map<String, Object> mParameters = new HashMap<>();//请求参数

    private
    @Constants.HttpMethod
    int mHttpMethod;//请求类型  必要参数

    private
    @Constants.ContentType
    int mContentType;

    private
    @Constants.RequestStrategy
    int mDefaultRequestStrategy;//数据请求策略
    private
    @Constants.RequestStrategy
    int mExpireRequestStrategy;//数据过期请求策略

    /*初始化默认值*/ {
        mApiDomain = NetCoreInitializer.getInstance().getBaseUrl(); //默认域名

        /*默认不走cache*/
        mCacheId = null;
        mMemoryCache = false;
        mDBCache = false;

        /*默认请求直接请求网络*/
        mDefaultRequestStrategy = STRATEGY_ONLY_NET;
        mExpireRequestStrategy = STRATEGY_NONE;
        mRespExpire = Request.RESPONSE_VALID_THRESHOLD;

        mHttpMethod = HTTP_METHOD_POST; //默认请求类型为Post
        mContentType = CONTENT_TYPE_JSON; //默认请求类型application/json
    }

    ObjectRequestBuilder(@NonNull String apiPath) {
        Preconditions.checkNotNull(apiPath);

        this.mApiPath = apiPath;
    }

    /**
     * 配置post请求的请求content类型，默认application/json
     *
     * @param contentType
     * @return
     */
    public ObjectRequestBuilder contentType(@Constants.ContentType int contentType) {
        Preconditions.checkArgument(mHttpMethod == HTTP_METHOD_POST, "只有post请求才需要配置Content-Type,默认请求方式为post");

        mContentType = contentType;
        return this;
    }

    /**
     * 配置http 请求方式 eg.http、post
     *
     * @param method
     * @return
     */
    public ObjectRequestBuilder httpMethod(@Constants.HttpMethod int method) {
        mHttpMethod = method;
        return this;
    }

    /**
     * 配置数据请求的策略 direct_net、force_net、level3_cache
     *
     * @param requestStrategy
     * @return
     */
    public ObjectRequestBuilder requestDefaultStrategy(@Constants.RequestStrategy
                                                               int requestStrategy) {
        mDefaultRequestStrategy = requestStrategy;
        return this;
    }

    /**
     * 配置数据过期请求的策略 direct_net、force_net、level3_cache
     *
     * @param requestStrategy
     * @return
     */
    public ObjectRequestBuilder requestExpireStrategy(@Constants.RequestStrategy
                                                              int requestStrategy) {
        mExpireRequestStrategy = requestStrategy;
        return this;
    }

    /**
     * 配置映射json数据的bean class
     *
     * @param cls
     * @return
     */
    public ObjectRequestBuilder dataClass(@NonNull Class cls) {
        Preconditions.checkNotNull(cls);

        mDataClass = cls;
        return this;
    }

    /**
     * 配置请求的域名
     *
     * @param domain
     * @return
     */
    public ObjectRequestBuilder domain(@NonNull String domain) {
        Preconditions.checkNotNull(domain);

        mApiDomain = domain;
        return this;
    }

    /**
     * 配置请求id，用语取消请求，必须指定
     *
     * @param requestId
     * @return
     */
    public ObjectRequestBuilder requestId(@NonNull String requestId) {
        Preconditions.checkNotNull(requestId);

        mRequestId = requestId;
        return this;
    }

    /**
     * 配置缓存对象时使用的id
     *
     * @param cacheId
     * @return
     */
    public ObjectRequestBuilder cacheId(@NonNull String cacheId) {
        Preconditions.checkNotNull(cacheId);

        mCacheId = cacheId;
        return this;
    }

    /**
     * 配置请求的参数信息
     *
     * @param key
     * @param value
     * @return
     */
    public ObjectRequestBuilder parameter(@NonNull String key, @NonNull Object value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        mParameters.put(key, value);
        return this;
    }

    /**
     * 配置解析网络返回数据时需要使用的json key
     *
     * @param value
     * @return
     */
    public ObjectRequestBuilder parseKey(@NonNull String value) {
        Preconditions.checkNotNull(value);

        mSegments.add(value);
        return this;
    }

    /**
     * 配置请求开启内存缓存数据功能
     *
     * @return
     */
    public ObjectRequestBuilder memoryCache() {
        mMemoryCache = true;
        return this;
    }

    /**
     * 配置请求开启db缓存数据功能
     *
     * @return
     */
    public ObjectRequestBuilder dbCache() {
        mDBCache = true;
        return this;
    }

    /**
     * 配置网络响应的有效时间,默认 Request.RESPONSE_VALID_THRESHOLD
     *
     * @param expireTime
     * @return
     */
    public ObjectRequestBuilder respExpire(long expireTime) {
        mRespExpire = expireTime;
        return this;
    }

    public Request build() {
        ObjectRequest request = new ObjectRequest(mDataClass);

        Preconditions.checkNotNull(mDataClass, "请求必须参数，用于json转换bean");
        Preconditions.checkNotNull(mApiPath, "请求必须参数");

        request.setUrl(mApiDomain + mApiPath);
        request.setCacheMemory(mMemoryCache);
        request.setCacheDB(mDBCache);
        request.setRespExpire(mRespExpire);
        request.setHttpMethod(mHttpMethod);
        request.setContentType(mContentType);
        request.setObjectParseKeys(mSegments);
        request.setDefaultRequestStrategy(mDefaultRequestStrategy);
        request.setExpireRequestStrategy(mExpireRequestStrategy);
        request.setRequestParameters(mParameters);

        if (!TextUtils.isEmpty(mRequestId)) {
            request.setRequestId(mRequestId);
        }

        //调用顺序不能调整
        request.setCacheId(mCacheId);

        return request;
    }


}

