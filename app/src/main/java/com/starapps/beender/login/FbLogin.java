package com.starapps.beender.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.starapps.beender.data.Profile;
import com.starapps.beender.data.db.AppDb;
import com.starapps.beender.utils.Prefs;

import org.json.JSONException;

import java.util.Arrays;



public class FbLogin {

    private static final String TAG = "FbLogin";

    private Activity activity;
    private LoginCallback mCallback;

    private CallbackManager callbackManager;

    public FbLogin(Activity activity, LoginCallback callback) {
        this.activity = activity;
        this.mCallback = callback;
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestData(loginResult);
            }

            @Override
            public void onCancel() {
                if (mCallback != null) mCallback.onFail();
            }

            @Override
            public void onError(FacebookException error) {
                if (mCallback != null) mCallback.onFail();
            }
        };
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
    }

    public void login() {
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "email"));
    }

    public void onDestroy() {
        LoginManager.getInstance().logOut();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void requestData(LoginResult loginResult) {
        if (loginResult != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    (object, response) -> {
                        if (object == null) {
                            if (mCallback != null) mCallback.onFail();
                        }
                        Profile profile = new Profile();
                        try {
                            profile.setEmail(object.getString("email"));
                            profile.setName(object.getString("name"));
                            profile.setGender(object.getString("gender"));
                            if (object.has("picture")) {
                                String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                profile.setPhotoUrl(profilePicUrl);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Prefs.getInstance(activity).setCurrentUser(profile.getName());
                        AppDb.getDatabase(activity).dao().insertProfile(profile);
                        if (mCallback != null) mCallback.onSuccess();
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            if (mCallback != null) mCallback.onFail();
        }
    }

    public interface LoginCallback {
        void onSuccess();

        void onFail();
    }
}
