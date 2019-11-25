package com.bgenterprise.helpcentermodule.Network;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://apps.babbangona.com/helpcenter-api/";
    /*public static final String BASE_URL = "http://192.168.137.1/helpcenter-api/";*/

    public static  Retrofit retrofit = null;

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
