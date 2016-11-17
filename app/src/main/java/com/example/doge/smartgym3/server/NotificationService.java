package com.example.doge.smartgym3.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by neil on 15/10/16.
 */


public interface NotificationService {

    @GET("/notification/me/")
    Call<JsonArray> getNotificationsForCurrentUser();



    /**
     * 200 if results
     * 204 if no reuslts
     */
    @FormUrlEncoded
    @POST("/notification/brag/")
    Call<JsonObject> brag(
            @Field("to") int to,
            @Field("weight") int weight,
            @Field("exercise") String exercise_type,
            @Field("exerciseID") int exercise_id
    );

}


