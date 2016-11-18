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

/**
 * Created by neil on 15/10/16.
 */


public interface ExerciseService {

    @GET("/user/me/")
    Call<JsonObject> getExercisesForCurrentUser();

    @GET("/user/{id}/exercises/")
    Call<JsonArray> getExercisesForUser(@Path("id")int id);

    @GET("/exercise/{id}/")
    Call<JsonObject> getExercise(@Path("id") int id);

    @DELETE("/exercise/{id}/")
    Call<Integer> deleteExercise(@Path("id") int id);

    /**
     * Used to create a new exercise
     *  - Call must be made by an authenticated user
     *  - Will return the following on success
     *  {
     "user": 2,
     "id": 1,
     "targetReps": 5,
     "targetSets": 5,
     "restTime": 5,
     "station": 1,
     "sets": []
     }
     */

    @FormUrlEncoded
    @POST("/exercise/")
    Call<JsonObject> startNewExercise(
        @Field("targetReps") int targetReps,
        @Field("targetSets") int targetSets,
        @Field("station") int stationId,
        @Field("restTime") int restTime,
        @Field("weight") int weight,
        @Field("exercise_type") String exercise_type

    );



    /**
     * Used to add sets to an exercise
     */

    @POST("/exerciseSet/")
    Call<JsonObject> addSet(
            @Field("exercise") int exercise,
            @Field("successfulSets") int successfulSets,
            @Field("time") int time
    );
}


