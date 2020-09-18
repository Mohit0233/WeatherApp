package com.example.weatherapp

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

class MainUiViewModel : ViewModel() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    var defaultLocation: LatLng = LatLng(-33.8523341, 151.2106085)
    private var lastKnownLocation: Location? = null


     var textResultForHomeFragment =  MutableLiveData<WeatherData>()
     var textResultForDashboardFragment = MutableLiveData<WeatherData>()

    /*var textResultForHomeFragment: LiveData<WeatherData> = _textResultForHomeFragmentPrivate
    var textResultForDashboardFragment: LiveData<WeatherData> = _textResultForDashboardFragmentPrivate*/

    fun getDeviceLocation(locationPermissionGranted: MutableLiveData<Boolean>) {
        try {
            if (locationPermissionGranted.value!!) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            defaultLocation =
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                )
                            textResultForHomeFragment = ApiCallback().called(defaultLocation)
                            textResultForDashboardFragment = ApiCallback().called(defaultLocation)
                        }
                    }
                }
            }
            else {
                defaultLocation = LatLng(-33.8523341, 151.2106085)
                textResultForHomeFragment = ApiCallback().called(defaultLocation)
                textResultForDashboardFragment = ApiCallback().called(defaultLocation)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


}