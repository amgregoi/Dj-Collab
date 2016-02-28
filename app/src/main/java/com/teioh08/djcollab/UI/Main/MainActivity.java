package com.teioh08.djcollab.UI.Main;
// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Player;
import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.UI.Main.Fragments.HostFragment;
import com.teioh08.djcollab.UI.Main.Fragments.JoinFragment;
import com.teioh08.djcollab.R;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String CLIENT_ID = "d5a5ea60d29c4c75adde4bf2efadd8e4";
    private static final String REDIRECT_URI = "my-first-auth://callback";
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;

    @Bind(R.id.searchView) SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSearchView.setVisibility(View.GONE);

        String token = CredentialsHandler.getToken(this);
        if (token == null) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setScopes(new String[]{"playlist-read"})
                    .build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }


    }

    public void onLoginButtonClicked(View view) {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"playlist-read"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }

    private void logError(String msg) {
//        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }


    @OnClick(R.id.hostButton)
    public void onHostButtonClick(){
        Fragment host = new HostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container,host, TAG).addToBackStack(TAG).commit();
    }

    @OnClick(R.id.joinButton)
    public void onJoinButtonClick(){
        Fragment join = new JoinFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, join, TAG).addToBackStack(TAG).commit();

    }

}
