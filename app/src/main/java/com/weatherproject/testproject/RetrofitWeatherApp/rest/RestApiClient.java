package com.weatherproject.testproject.RetrofitWeatherApp.rest;

import android.net.Uri;

import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiClient {

    public static final String YAHOO_API_ENDPOINT = "https://query.yahooapis.com/v1/public/";
    private  static Retrofit retrofit = null;

    public static Retrofit getClient(){

        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(YAHOO_API_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
