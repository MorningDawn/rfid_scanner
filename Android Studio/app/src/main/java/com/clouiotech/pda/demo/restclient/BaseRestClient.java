package com.clouiotech.pda.demo.restclient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roka on 23/10/16.
 */
public class BaseRestClient {

    private static String BASE_URL = "http://rokarokaroka.com/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getRestClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
