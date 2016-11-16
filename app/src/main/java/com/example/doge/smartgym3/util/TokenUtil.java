package com.example.doge.smartgym3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.doge.smartgym3.BuildConfig;
import com.example.doge.smartgym3.R;
import com.example.doge.smartgym3.server.LoginService;
import com.facebook.AccessToken;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by neil on 16/10/16.
 */
public class TokenUtil {

    private JsonObject lastResult;

    static boolean isLoggedIntoFacebook(AccessToken accessToken) {
        accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static void getServerAccessTokenFromFacebookAccessToken(final Context context, AccessToken accessToken) {
        JsonObject resp = null;
        try {
            resp = convertFacebookTokenToLocal(accessToken).execute().body();
            saveAccessTokenToSharedPreferences(context,resp.get("access_token").getAsString());
        } catch (IOException e) {

            Log.wtf("ERROR", "Network error: Failed to convert accesstoken");
            Log.wtf("ERROR", e.toString());
//            throw new IOException("Failed to convert accessTOken");

        }


    }

    private static void saveAccessTokenToSharedPreferences(Context context, String accessToken) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String timetablrTokenKey =
                context.getResources().getString(R.string.internal_pref_smartgym_token_key);
        sharedPref.edit().putString(timetablrTokenKey, accessToken).apply();

    }

    public static String getAccessTokenFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String timetablrTokenKey =
                context.getResources().getString(R.string.internal_pref_smartgym_token_key);
        String accessToken = sharedPref.getString(timetablrTokenKey, "");

        return accessToken;

    }

    private static Call<JsonObject> convertFacebookTokenToLocal(AccessToken accessToken) {

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        String clientId = BuildConfig.API_KEY;
        String clientSecret = BuildConfig.API_SECRET;
        String backend = "facebook";

        LoginService loginService =
                ServiceGenerator.createService(LoginService.class);
        Call<JsonObject> call = loginService.getAccessToken(
                "convert_token", // granttype
                clientId,
                clientSecret,
                backend,
                accessToken.getToken()
        );
        return call;





    }
}


