package com.clouiotech.pda.demo.restclient;

import com.clouiotech.pda.demo.BaseObject.ItemResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roka on 23/10/16.
 */
public class BaseRestClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRestClient(String baseUrl) {
        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

}
