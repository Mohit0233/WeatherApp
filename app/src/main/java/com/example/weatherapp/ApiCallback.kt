package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.WeatherData
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCallback {

    private val appId = "21d5615c0b94f68985f7079b0f592dd1"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: WeatherService = retrofit.create(WeatherService::class.java)


    var textResult = MutableLiveData<WeatherData>()

    fun called(location: LatLng): MutableLiveData<WeatherData> {

        val call: Call<WeatherData> = service.getPost(location.latitude.toString(),location.longitude.toString(), appId)

        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(
                call: Call<WeatherData>,
                response: Response<WeatherData>
            ) {

                textResult.value = response.body()
            }
            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                textResult.postValue(null)
                Log.e("Error","Error")
            }
        })
        return textResult
    }
}