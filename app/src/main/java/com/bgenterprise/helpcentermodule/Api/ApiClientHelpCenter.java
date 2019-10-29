package com.bgenterprise.helpcentermodule.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientHelpCenter {

    public static final String BASE_URL = "https://api.myjson.com/bins/";

    public static  Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        if (retrofit == null){

            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
