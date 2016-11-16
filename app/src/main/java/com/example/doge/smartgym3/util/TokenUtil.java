package com.example.doge.smartgym3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    void getTimetablrAccessTokenFromFacebookAccessToken(final Context context, AccessToken accessToken) {
        JsonObject resp = null;
        try {
            resp = this.convertFacebookTokenToLocal(accessToken).execute().body();
            saveAccessTokenToSharedPreferences(context,resp.get("access_token").getAsString());
        } catch (IOException e) {

        }


    }

    private void saveAccessTokenToSharedPreferences(Context context, String accessToken) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String timetablrTokenKey =
                context.getResources().getString(R.string.internal_pref_smartgym_token_key);
        sharedPref.edit().putString(timetablrTokenKey, accessToken).apply();

        // TODO(doyle): Remove on confirm replacement code works
        /*
            String sharedPrefFile = context.getResources().getString(R.string.preference_file_key);
            SharedPreferences.Editor editor = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE).edit();
            editor.putString("timetablrAccessToken", accessToken);
        */
    }

    public static String getAccessTokenFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String timetablrTokenKey =
                context.getResources().getString(R.string.internal_pref_smartgym_token_key);
        String accessToken = sharedPref.getString(timetablrTokenKey, "");

        return accessToken;

        // TODO(doyle): Remove on confirm replacement code works
        /*
        String sharedPrefFile = context.getResources().getString(R.string.preference_file_key);
        SharedPreferences prefs = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        return (prefs.getString("timetablrAccessToken", null));//null is the default value.
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
        }
        return null;
        */
    }

    private Call<JsonObject> convertFacebookTokenToLocal(AccessToken accessToken) {

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        String clientId = BuildConfig.TIMETABLR_KEY;
        String clientSecret = BuildConfig.TIMETABLR_SECRET;
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


