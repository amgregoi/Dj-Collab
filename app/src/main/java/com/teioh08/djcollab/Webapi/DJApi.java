package com.teioh08.djcollab.Webapi;


import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class DJApi {

    private static DJService REST_CLIENT;
    private static String ROOT = "http://djcollab.com";

    static {
        setupRestClient();
    }

    private DJApi() {
    }

    public static DJService get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .build();

        REST_CLIENT = restAdapter.create(DJService.class);
    }
}