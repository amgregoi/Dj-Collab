package com.teioh08.djcollab.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.teioh08.djcollab.DJApplication;
import com.teioh08.djcollab.Webapi.DJApi;

import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

    private static final String ACCESS_TOKEN_NAME = "webapi.credentials.access_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_AT = "expires_at";

    public static final String CLIENT_ID = "94978b3f97cc4e1c8e73e6a264b8a4b5";                      //TODO: move later
    public static final String REDIRECT_URI = "http://djcollab.com/hostparty/callback/";          //TODO: move later
    public static final int REQUEST_CODE = 1337;                                                  //TODO: move later


    public static void setToken(String token, long expiresIn, TimeUnit unit) {
        Context appContext = DJApplication.getInstance();

        long now = System.currentTimeMillis();
        long expiresAt = now + unit.toMillis(expiresIn);

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.putLong(EXPIRES_AT, expiresAt);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(ACCESS_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken() {
        Context appContext = DJApplication.getInstance();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        String token = sharedPref.getString(ACCESS_TOKEN, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        return token;
    }
}

