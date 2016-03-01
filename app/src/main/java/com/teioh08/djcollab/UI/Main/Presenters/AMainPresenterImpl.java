package com.teioh08.djcollab.UI.Main.Presenters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.teioh08.djcollab.UI.Main.View.AMainActivity;
import com.teioh08.djcollab.UI.Main.View.Mappers.AMainMapper;
import com.teioh08.djcollab.Utils.CredentialsHandler;

import java.util.concurrent.TimeUnit;

public class AMainPresenterImpl implements  AMainPresenter{
    final public static String TAG = AMainPresenterImpl.class.getSimpleName();

    private AMainMapper mAMainMap;
    private Context mAMainContext;

    private static final String CLIENT_ID = "d5a5ea60d29c4c75adde4bf2efadd8e4";     //TODO: move later
    private static final String REDIRECT_URI = "my-first-auth://callback";          //TODO: move later
    private static final int REQUEST_CODE = 1337;                                   //TODO: move later

    public AMainPresenterImpl(AMainMapper map){
        mAMainMap = map;
        mAMainContext = map.getContext();
    }

    @Override
    public void init(Bundle bundle) {

    }

    @Override
    public void onSavedState() {

    }

    @Override
    public void onRestoreState() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        mAMainContext = null;
    }

    @Override
    public void spotifyAuthenticate() {
        String token = CredentialsHandler.getToken(mAMainContext);
        if (token == null) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setScopes(new String[]{"playlist-read, user-library-read, playlist-read-private, user-read-private, user-library-modify"})
                    .build();

            AuthenticationClient.openLoginActivity(((AMainActivity)mAMainMap), REQUEST_CODE, request);
        }
    }

    @Override
    public void spotifyAuthenticationResult(int requestCode, int resultCode, Intent intent) {
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(mAMainContext, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    break;

                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }

    private void logMessage(String msg) {
        //Toast.makeText(mAMainContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    private void logError(String msg) {
        //Toast.makeText(mAMainContext, msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

}
