package com.bgenterprise.helpcentermodule.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://apps.franchise-babbangona.com/helpcenter-api/";
    //private static final String BASE_URL = "http://172.168.1.86/helpcenter-api/";

    private static  Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        //For increasing the timeout of Retrofit. Default is normally 10 seconds.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        if (retrofit == null){

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
