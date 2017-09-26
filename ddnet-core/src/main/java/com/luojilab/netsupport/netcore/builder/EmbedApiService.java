package com.luojilab.netsupport.netcore.builder;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by liushuo on 2017/5/26.
 */

public interface EmbedApiService {
    @POST()
    Call<JsonObject> RetrofitPostBodyApi(@Url String url,@Body JsonObject param);

    @FormUrlEncoded
    @POST()
    Call<JsonObject> RetrofitPostFormApi(@Url String url,@FieldMap() Map<String, String> formParam);

    @GET()
    Call<JsonObject> RetrofitGetApi( @Url String url,@QueryMap Map<String, String> options);

}
