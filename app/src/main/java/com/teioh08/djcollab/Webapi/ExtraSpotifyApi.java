package com.teioh08.djcollab.Webapi;


import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ExtraSpotifyApi {

    private static ExtraSpotifyService REST_CLIENT;
    private static String ROOT = "https://api.spotify.com/v1";

    static {
        setupRestClient();
    }

    private ExtraSpotifyApi() {
    }

    public static ExtraSpotifyService get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .build();

        REST_CLIENT = restAdapter.create(ExtraSpotifyService.class);
    }
}