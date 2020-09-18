package com.example.weatherapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.MainUiViewModel
import com.example.weatherapp.R
import com.example.weatherapp.adapter.DayWeatherAdapter
import com.example.weatherapp.adapter.HourlyWeatherAdapter
import com.example.weatherapp.list.DayWeatherItem
import com.example.weatherapp.list.HourlyWeatherItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private val mainUiViewModel: MainUiViewModel by activityViewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hourlyWeatherViewAdapter: RecyclerView.Adapter<*>
    private lateinit var hourlyWeatherViewManager: RecyclerView.LayoutManager

    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var dayWeatherViewAdapter: RecyclerView.Adapter<*>
    private lateinit var dayWeatherViewManager: RecyclerView.LayoutManager

    private var hourlyWeatherList = ArrayList<HourlyWeatherItem>()
    private var dayWeatherList = ArrayList<DayWeatherItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val tempTextView: TextView = root.findViewById(R.id.tempTextView)
        homeViewModel.text.observe(viewLifecycleOwner, {
            tempTextView.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


        hourlyWeatherViewManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL,false)
        dayWeatherViewManager = LinearLayoutManager(view.context)

       /* var hi = mainUiViewModel.textResultForHomeFragment.value
        Log.e("hi", hi.toString())
        hi = mainUiViewModel._textResultForHomeFragmentPrivate.value
        Log.e("hi once again", hi.toString())
        Log.e("default Location", mainUiViewModel.defaultLocation.toString())*/
        mainUiViewModel.textResultForHomeFragment.observe(viewLifecycleOwner, {

            if (it != null) {
                var temp = it.current.temp.toString() + " "
                tempTextView.text = temp
                temp = it.current.weather[0].main + " "
                condTextView.text = temp
                temp = "${it.daily[0].temp.min}/${it.daily[0].temp.max}°C"
                maxMinTempTExtView.text = temp
                temp = intUTCToDateDay(it.current.dt) + " "
                dayOfWeekTextView.text = temp
                temp = it.current.feels_like.toString() + " "
                tempFeltTextView.text = temp
                temp = it.current.visibility.toString() + " "
                visTextView.text = temp
                temp = it.current.pressure.toString() + " "
                airPreTextView.text = temp
                temp = it.current.uvi.toString() + " "
                uVTextView.text = temp
                temp = it.current.humidity.toString() + " "
                humTextView.text = temp
                temp = it.current.wind_speed.toString() + " "
                nWTextView.text = temp

                for (i in it.hourly.indices) {
                    hourlyWeatherList.plusAssign(
                        HourlyWeatherItem(
                            intUTCToDateHour(it.hourly[i].dt),
                            null,
                            it.hourly[i].weather[0].main,
                            "${it.hourly[i].temp}°C"
                        )
                    )
                }

                for (i in it.daily.indices) {
                    dayWeatherList.plusAssign(
                        DayWeatherItem(
                            intUTCToDate(it.daily[i].dt),
                            if (i == 0) {
                                "Today"
                            } else {
                                intUTCToDateDay(it.daily[i].dt)
                            },
                            null,
                            it.daily[i].weather[0].main,
                            "${it.daily[i].temp.min}/${it.daily[i].temp.max}°C"
                        )
                    )
                }

                hourlyWeatherViewAdapter = HourlyWeatherAdapter(hourlyWeatherList)
                hourlyRecyclerView = hourlyWeatherRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = hourlyWeatherViewManager
                    adapter = hourlyWeatherViewAdapter

                }

                dayWeatherViewAdapter = DayWeatherAdapter(dayWeatherList)
                dayRecyclerView = dayWeatherRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = dayWeatherViewManager
                    adapter = dayWeatherViewAdapter

                }

                Toast.makeText(this.context, "${it.lat} && ${it.lon} ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun intUTCToDateDay(p0: Int): String {
        val time = (p0.toString() + "000").toLong()
        // dd MMM yyyy HH:mm:ss a
        // yyyy-MM-dd HH:mm:ss.SSS Z
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(time))
    }

    private fun intUTCToDateHour(p0: Int): String {
        val time = (p0.toString() + "000").toLong()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(time))
    }

    private fun intUTCToDate(p0: Int): String {
        val time = (p0.toString() + "000").toLong()
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(time))
    }


}
