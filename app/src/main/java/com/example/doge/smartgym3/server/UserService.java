package com.example.doge.smartgym3.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by neil on 15/10/16.
 */


public interface UserService {


    @GET("/user/{id}/")
    Call<JsonObject> getUser(@Path("id") int id);


    /**
     *
     * Sample Response
     *
     * [
     *        {
     *            "weight": 100,
     *            "surname": "Aladajafffjgg McDonaldwitz",
     *            "user_id": 4,
     *            "firstname": "Harry"
     *        },
     *        {
     *            "weight": 100,
     *            "surname": "Aladbdahdbegb Bushaksen",
     *            "user_id": 3,
     *            "firstname": "Patricia"
     *        },
     *        {
     *            "weight": 103,
     *            "surname": "Aladjfjgdfgad Bushaksen",
     *            "user_id": 5,
     *             "firstname": "Ruth"
     *        }
     *    ]
     *
     * @param exercise
     * @param weight
     * @return
     */

    @GET("/user/getFriendsWithClosestResults/")
    Call<JsonArray> getFriendsToBragTo(
            @Query("exerciseTypeFilter") String exercise,
            @Query("userAccomplishedWeight") int weight
    );


}


