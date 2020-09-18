package com.example.weatherapp.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.ApiCallback
import com.example.weatherapp.MainUiViewModel
import com.example.weatherapp.R
import com.example.weatherapp.adapter.DayWeatherAdapter
import com.example.weatherapp.adapter.HourlyWeatherAdapter
import com.example.weatherapp.list.DayWeatherItem
import com.example.weatherapp.list.HourlyWeatherItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_persistent_bottom_sheet.view.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment(), GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener, OnMapReadyCallback {

    private lateinit var dashboardViewModel: DashboardViewModel
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null



    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hourlyWeatherViewAdapter: RecyclerView.Adapter<*>
    private lateinit var hourlyWeatherViewManager: RecyclerView.LayoutManager

    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var dayWeatherViewAdapter: RecyclerView.Adapter<*>
    private lateinit var dayWeatherViewManager: RecyclerView.LayoutManager

    private var hourlyWeatherList = ArrayList<HourlyWeatherItem>()
    private var dayWeatherList = ArrayList<DayWeatherItem>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        lifecycleScope.launch {

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
            mapFragment?.getMapAsync(this@DashboardFragment)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(root.bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Toast.makeText(this@DashboardFragment.requireContext(), "STATE_COLLAPSED", Toast.LENGTH_SHORT).show()
                        root.bottomSheet.background = (ContextCompat.getDrawable(requireContext(), R.drawable.round))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Toast.makeText(this@DashboardFragment.requireContext(), "STATE_EXPANDED", Toast.LENGTH_SHORT).show()
                        root.bottomSheet.background = (ContextCompat.getDrawable(requireContext(), R.drawable.edge))
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(
                        this@DashboardFragment.requireContext(),
                        "STATE_DRAGGING",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(
                        this@DashboardFragment.requireContext(),
                        "STATE_SETTLING",
                        Toast.LENGTH_SHORT
                    ).show()
                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(
                        this@DashboardFragment.requireContext(),
                        "STATE_HIDDEN",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        this@DashboardFragment.requireContext(),
                        "OTHER_STATE",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_dashboard)
        mainUiViewModel.textResultForDashboardFragment.observe(viewLifecycleOwner, {
            textView.text = it.toString()
        })

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


        hourlyWeatherViewManager = LinearLayoutManager(view.context,
            LinearLayoutManager.HORIZONTAL,false)
        dayWeatherViewManager = LinearLayoutManager(view.context)


        mainUiViewModel.textResultForDashboardFragment.observe(viewLifecycleOwner, {

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
                            if (i == 0){ "Today" }else { intUTCToDateDay(it.daily[i].dt) },
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

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnCameraIdleListener(this)
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    override fun onMapClick(point: LatLng) {
        mainUiViewModel.textResultForDashboardFragment = ApiCallback().called(point)
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        Toast.makeText(this.context, "click $point", Toast.LENGTH_SHORT).show()
    }

    override fun onMapLongClick(point: LatLng) {
        try {
            val manager: FragmentManager =
                (this.context as AppCompatActivity).supportFragmentManager
            CustomBottomSheetDialogFragment().show(manager, CustomBottomSheetDialogFragment.TAG)
        } catch (e: Exception) {
            Log.e("❤", e.printStackTrace().toString())
        }
        Toast.makeText(this.context, "long click", Toast.LENGTH_SHORT).show()
    }

    override fun onCameraIdle() {
        //if(!::map.isInitialized) return
        //cameraTextView.text = map.cameraPosition.toString()
        //Toast.makeText(this.context, "camera idle", Toast.LENGTH_SHORT).show()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            Toast.makeText(this.context, "Some exception occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

}