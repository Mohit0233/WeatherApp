package com.example.weatherapp.data.repository

import com.example.weatherapp.data.network.WeatherApi

class WeatherRepository(private val api: WeatherApi) : BaseRepository() {

    suspend fun getWeather(
        lat: String,
        lon: String,
        app_id: String
    ) = safeApiCall {
        api.getPost(lat, lon, app_id)
    }

}