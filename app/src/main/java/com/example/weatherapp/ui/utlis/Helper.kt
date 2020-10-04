package com.example.weatherapp.ui.utlis

import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.adapter.DayWeatherAdapter
import com.example.weatherapp.data.adapter.HourlyWeatherAdapter
import com.example.weatherapp.data.list.DayWeatherItem
import com.example.weatherapp.data.list.HourlyWeatherItem
import com.example.weatherapp.data.responses.WeatherData
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.layout_weather_data.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


var iconMap = mapOf(
    "01d" to R.drawable.png_01d,
    "01n" to R.drawable.png_01n,
    "02d" to R.drawable.png_02d,
    "02n" to R.drawable.png_02n,
    "03d" to R.drawable.png_03d,
    "03n" to R.drawable.png_03n,
    "04d" to R.drawable.png_04d,
    "04n" to R.drawable.png_04n,
    "09d" to R.drawable.png_09d,
    "09n" to R.drawable.png_09n,
    "10d" to R.drawable.png_10d,
    "10n" to R.drawable.png_10n,
    "11d" to R.drawable.png_11d,
    "11n" to R.drawable.png_11n,
    "13d" to R.drawable.png_13d,
    "13n" to R.drawable.png_13n,
    "50d" to R.drawable.png_50d,
    "50n" to R.drawable.png_50n,
)

fun updateUi(view: View, weatherData: WeatherData, isHomeFragment: Boolean) {

    var temp = "7-Day Weather Report"
    view.weekWeatherReportTextView.text = temp
    temp = "Weather Details"
    view.weatherDetailsTextView.text = temp
    temp = "Temperature Felt"
    view.temperatureFeltTextView.text = temp
    temp = "Visibility"
    view.visibilityTextView.text = temp
    temp = "Air Pressure"
    view.airPressureTextView.text = temp
    temp = "UV"
    view.ultraVioletTextView.text = temp
    temp = "Humidity"
    view.humidityTextView.text = temp
    temp = "NW"
    view.nWDescriptionTextView.text = temp

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

    temp = weatherData.current.temp.toString() + " "
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
                iconMap[weatherData.hourly[i].weather[0].icon],
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
                iconMap[weatherData.daily[i].weather[0].icon],
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
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isHomeFragment) {
                    view.swipeToRefresh.isEnabled =
                        !(newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING
                                || newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                }
            }
        })
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