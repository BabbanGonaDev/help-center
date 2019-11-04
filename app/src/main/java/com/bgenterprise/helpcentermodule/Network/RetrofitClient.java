package com.bgenterprise.helpcentermodule.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://192.168.8.53/helpcenter-api/";

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
