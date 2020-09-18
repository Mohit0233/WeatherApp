package com.example.weatherapp

import com.example.weatherapp.data.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    //https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&appid={appId}
    @GET("onecall?units=metric")
    fun getPost(@Query("lat") lat: String, @Query("lon") lon: String, @Query("APPID") app_id: String): Call<WeatherData>
}