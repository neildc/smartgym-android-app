package com.example.doge.smartgym3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;
    private WorkoutApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (WorkoutApplication) getApplication();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.wtf("WTF", String.valueOf(loginResult.getAccessToken()));
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name");

                        new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/" + loginResult.getAccessToken().getUserId(),
                                parameters,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        JSONObject object = response.getJSONObject();
                                        try {
                                            app.setName(object.getString("name"));
                                            Log.wtf("WTF", object.getString("name"));
                                            Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } catch (JSONException e) {
                                            Log.wtf("WTF", e);
                                        }
                                    }
                                }
                        ).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.wtf("WTF", "CANCELLED");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.wtf("WTF","REJECTED");
                    }
                });
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }
}
