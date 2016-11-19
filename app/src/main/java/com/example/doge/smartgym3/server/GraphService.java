package com.example.doge.smartgym3.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by neil on 15/10/16.
 */


public interface GraphService {
//    [
//        {
//            "user": "Betty",
//            "data": {
//                "6": 800,
//                "17": 350
//            }
//        },
//        {
//            "user": "Ruth",
//            "data": {
//                "17": 300
//            }
//        }
//    ]
//    in data <TIME>:<VOLUME(KG)>

    @GET("/exercise/graphFriendsVolumeDay/")
    Call<JsonArray> getGraphFriendsVolumeForADay(@Query("exercise") String exercise);


    @GET("/exercise/graphFriendsVolumeHour/")
    Call<JsonArray> getGraphFriendsVolumeForAHour(@Query("exercise") String exercise);

    @GET("/exercise/graphFriendsVolumeMinute/")
    Call<JsonArray> getGraphFriendsVolumeForAMinute(@Query("exercise") String exercise);
}


