package com.dynamiclist.api;


import com.dynamiclist.model.Facts;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by Saravanan on 3/7/2016.
 * Retrofit API interface for managing url calls like GET,POST..etc.
 * This is the service class
 */
public interface FactsAPI {

    @GET("/u/746330/facts.json")
    Call<Facts> getFacts();
}
