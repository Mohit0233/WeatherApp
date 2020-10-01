package com.example.weatherapp.data.network

import com.example.weatherapp.data.responses.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    //https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&appid={appId}
    @GET("onecall?units=metric")
    suspend fun getPost(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") app_id: String
    ): WeatherData
}