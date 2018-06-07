package com.weatherproject.testproject.RetrofitWeatherApp.rest;

import com.weatherproject.testproject.RetrofitWeatherApp.models.JsonWeatherResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiEndpointInterface {

   /* @GET("{city}")
    Call<Forecast> getForecast(@Path("city") String city);
*/
    @GET("yql")
    Call<JsonWeatherResponse> getForecast(@Query("q") String query, @Query("format") String format);

}