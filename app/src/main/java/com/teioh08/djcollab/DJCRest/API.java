package com.teioh08.djcollab.DJCRest;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("/weather")
    void getWeather(@Query("q") String cityName,
                    Callback<String> callback);
}

