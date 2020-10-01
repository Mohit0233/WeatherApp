package com.example.weatherapp.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.data.network.Resource
import com.example.weatherapp.data.network.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.responses.WeatherData
import com.example.weatherapp.databinding.FragmentDashboardBinding
import com.example.weatherapp.ui.base.BaseFragment
import com.example.weatherapp.ui.utlis.updateUi
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

class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, WeatherRepository>(),
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnCameraIdleListener,
    OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var weatherData: WeatherData? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            weatherData = savedInstanceState.getParcelable(KEY_WEATHER_DATA)
        }*/
        Log.e("Weather Data", weatherData.toString())

        lifecycleScope.launch {

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
            mapFragment?.getMapAsync(this@DashboardFragment)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.root.bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Toast.makeText(
                            this@DashboardFragment.requireContext(),
                            "STATE_COLLAPSED",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.root.bottomSheet.background =
                            (ContextCompat.getDrawable(requireContext(), R.drawable.round))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Toast.makeText(
                            this@DashboardFragment.requireContext(),
                            "STATE_EXPANDED",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.root.bottomSheet.background =
                            (ContextCompat.getDrawable(requireContext(), R.drawable.edge))
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

        viewModel.weatherResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    updateUi(binding.root, it.value)
                    weatherData = it.value
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Damn, Failed To Load Data",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })

    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_WEATHER_DATA, weatherData as Parcelable)
    }
*/
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
        viewModel.getWeather(
            point.latitude.toString(),
            point.longitude.toString(),
            app_id
        )
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
                            viewModel.getWeather(
                                lastKnownLocation!!.latitude.toString(),
                                lastKnownLocation!!.longitude.toString(),
                                app_id
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

    override fun getViewModal() = DashboardViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        WeatherRepository(remoteDataSource.buildApi(WeatherApi::class.java))


    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        private const val KEY_WEATHER_DATA = "weather_data"
        const val app_id = "21d5615c0b94f68985f7079b0f592dd1"
    }


}