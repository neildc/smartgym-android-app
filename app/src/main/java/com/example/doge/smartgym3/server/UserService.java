package com.example.doge.smartgym3.server;

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


public interface UserService {

    @GET("/user/me/")
    Call<JsonObject> getCurrentUser();

    @GET("/user/{id}/")
    Call<JsonObject> getUser(@Path("id") int id);

}


