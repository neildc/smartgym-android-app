package com.example.doge.smartgym3.server;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by neil on 15/10/16.
 */
public interface LoginService {
    @FormUrlEncoded
    @POST("/auth/convert-token")
    Call<JsonObject> getAccessToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("backend") String backend,
            @Field("token") String token
    );
}


