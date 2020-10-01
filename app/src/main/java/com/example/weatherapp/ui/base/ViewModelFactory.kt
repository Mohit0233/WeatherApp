package com.example.weatherapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.BaseRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.dashboard.DashboardViewModel
import com.example.weatherapp.ui.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as WeatherRepository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                repository as WeatherRepository
            ) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}