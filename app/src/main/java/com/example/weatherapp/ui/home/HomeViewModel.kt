package com.example.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.network.Resource
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.responses.WeatherData
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherResponse: MutableLiveData<Resource<WeatherData>> = MutableLiveData()
    val weatherResponse: LiveData<Resource<WeatherData>>
        get() = _weatherResponse

    fun getWeather(
        lat: String,
        lon: String,
        app_id: String
    ) = viewModelScope.launch {

        _weatherResponse.value = repository.getWeather(lat, lon, app_id)
    }

}