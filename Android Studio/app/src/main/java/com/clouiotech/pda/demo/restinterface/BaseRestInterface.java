package com.clouiotech.pda.demo.restinterface;

import com.clouiotech.pda.demo.BaseObject.ItemResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by roka on 23/10/16.
 */
public interface BaseRestInterface {
    @GET("tryjson")
    Call<ItemResponse> getTryJson(@Query("operation") int operation);
}
