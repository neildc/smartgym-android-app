package com.example.doge.smartgym3.server;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by neil on 15/10/16.
 */
public interface GCMDevicesService {
    @FormUrlEncoded
    @POST("/device/gcm/")
    Call<JsonObject> registerDevice(
            @Field("registration_id") String token
    );
}


