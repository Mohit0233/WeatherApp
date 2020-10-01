package com.example.weatherapp.ui.utlis

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.adapter.DayWeatherAdapter
import com.example.weatherapp.data.adapter.HourlyWeatherAdapter
import com.example.weatherapp.data.list.DayWeatherItem
import com.example.weatherapp.data.list.HourlyWeatherItem
import com.example.weatherapp.data.responses.WeatherData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun updateUi(view: View, weatherData: WeatherData) {

    val tempTextView: TextView = view.findViewById(R.id.tempTextView)
    val condTextView: TextView = view.findViewById(R.id.condTextView)
    val maxMinTempTExtView: TextView = view.findViewById(R.id.maxMinTempTextView)
    val dayOfWeekTextView: TextView = view.findViewById(R.id.dayOfWeekTextView)
    val tempFeltTextView: TextView = view.findViewById(R.id.tempFeltTextView)
    val visTextView: TextView = view.findViewById(R.id.visTextView)
    val airPreTextView: TextView = view.findViewById(R.id.airPreTextView)
    val uVTextView: TextView = view.findViewById(R.id.uVTextView)
    val humTextView: TextView = view.findViewById(R.id.humTextView)
    val nWTextView: TextView = view.findViewById(R.id.nWTextView)
    val hourlyWeatherRecyclerView: RecyclerView =
        view.findViewById(R.id.hourlyWeatherRecyclerView)

    val dayWeatherRecyclerView: RecyclerView =
        view.findViewById(R.id.dayWeatherRecyclerView)


    val hourlyWeatherViewManager = LinearLayoutManager(
        view.context,
        LinearLayoutManager.HORIZONTAL, false
    )
    val dayWeatherViewManager = LinearLayoutManager(view.context)


    var temp = weatherData.current.temp.toString() + " "
    tempTextView.text = temp
    temp = weatherData.current.weather[0].main + " "
    condTextView.text = temp
    temp = "${weatherData.daily[0].temp.min}/${weatherData.daily[0].temp.max}°C"
    maxMinTempTExtView.text = temp
    temp = intUTCToDateDay(weatherData.current.dt) + " "
    dayOfWeekTextView.text = temp
    temp = weatherData.current.feels_like.toString() + " "
    tempFeltTextView.text = temp
    temp = weatherData.current.visibility.toString() + " "
    visTextView.text = temp
    temp = weatherData.current.pressure.toString() + " "
    airPreTextView.text = temp
    temp = weatherData.current.uvi.toString() + " "
    uVTextView.text = temp
    temp = weatherData.current.humidity.toString() + " "
    humTextView.text = temp
    temp = weatherData.current.wind_speed.toString() + " "
    nWTextView.text = temp


    val hourlyWeatherList = ArrayList<HourlyWeatherItem>()
    val dayWeatherList = ArrayList<DayWeatherItem>()

    for (i in weatherData.hourly.indices) {
        hourlyWeatherList.plusAssign(
            HourlyWeatherItem(
                intUTCToDateHour(weatherData.hourly[i].dt),
                null,
                weatherData.hourly[i].weather[0].main,
                "${weatherData.hourly[i].temp}°C"
            )
        )
    }

    for (i in weatherData.daily.indices) {
        dayWeatherList.plusAssign(
            DayWeatherItem(
                intUTCToDate(weatherData.daily[i].dt),
                if (i == 0) {
                    "Today"
                } else {
                    intUTCToDateDay(weatherData.daily[i].dt)
                },
                null,
                weatherData.daily[i].weather[0].main,
                "${weatherData.daily[i].temp.min}/${weatherData.daily[i].temp.max}°C"
            )
        )
    }


    val hourlyWeatherViewAdapter = HourlyWeatherAdapter(hourlyWeatherList)
    hourlyWeatherRecyclerView.apply {
        setHasFixedSize(true)
        layoutManager = hourlyWeatherViewManager
        adapter = hourlyWeatherViewAdapter

    }


    val dayWeatherViewAdapter = DayWeatherAdapter(dayWeatherList)
    dayWeatherRecyclerView.apply {
        setHasFixedSize(true)
        layoutManager = dayWeatherViewManager
        adapter = dayWeatherViewAdapter

    }
}


fun intUTCToDateDay(p0: Int): String {
    val time = (p0.toString() + "000").toLong()
    // dd MMM yyyy HH:mm:ss a
    // yyyy-MM-dd HH:mm:ss.SSS Z
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(time))
}

fun intUTCToDateHour(p0: Int): String {
    val time = (p0.toString() + "000").toLong()
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(time))
}

fun intUTCToDate(p0: Int): String {
    val time = (p0.toString() + "000").toLong()
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(time))
}