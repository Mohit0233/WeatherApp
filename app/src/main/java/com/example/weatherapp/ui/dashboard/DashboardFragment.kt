package com.example.weatherapp.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R
import com.example.weatherapp.data.network.Resource
import com.example.weatherapp.data.network.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.responses.WeatherData
import com.example.weatherapp.databinding.FragmentDashboardBinding
import com.example.weatherapp.ui.base.BaseFragment
import com.example.weatherapp.ui.utlis.handleApiError
import com.example.weatherapp.ui.utlis.snackbar
import com.example.weatherapp.ui.utlis.updateUi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_persistent_bottom_sheet.view.*

class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, WeatherRepository>(),
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnCameraIdleListener,
    OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private val defaultLocation = LatLng(29.7911013062734, 76.40132917643)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location = Location("")
    private var weatherData: WeatherData? = null
    private var marker: Marker? = null
    private val apiKey = getString(R.string.weather_api_key)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)!!
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            weatherData = savedInstanceState.getParcelable(KEY_WEATHER_DATA)
        }
        Log.e("Weather Data", weatherData.toString())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        bottomSheetBehavior = BottomSheetBehavior.from(binding.root.bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) { /*handle onSlide*/
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.root.bottomSheet.background =
                            (ContextCompat.getDrawable(requireContext(), R.drawable.round))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.root.bottomSheet.background =
                            (ContextCompat.getDrawable(requireContext(), R.drawable.edge))
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.weatherResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    updateUi(binding.root, it.value, false)
                    weatherData = it.value
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        viewModel.getWeather(
                            lastKnownLocation.latitude.toString(),
                            lastKnownLocation.longitude.toString(),
                            apiKey
                        )
                    }
                }
            }
        })

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
        if (marker == null) {
            marker = map?.addMarker(
                MarkerOptions()
                    .position(point)
                    .title("${point.latitude},${point.longitude} is the location")
            )
        } else {
            marker!!.position = point
            marker!!.title = "${point.latitude},${point.longitude} is the location"
        }
        lastKnownLocation = Location("")
        lastKnownLocation.latitude = point.latitude
        lastKnownLocation.longitude = point.longitude
        viewModel.getWeather(
            point.latitude.toString(),
            point.longitude.toString(),
            apiKey
        )
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        Toast.makeText(this.context, "click $point", Toast.LENGTH_SHORT).show()
    }

    override fun onMapLongClick(point: LatLng) {
        try {
            val manager: FragmentManager = childFragmentManager
            CustomBottomSheetDialogFragment().show(manager, CustomBottomSheetDialogFragment.TAG)
        } catch (e: Exception) {
            Log.e("Exception in omMapLongClick", e.printStackTrace().toString())
        }
    }

    override fun onCameraIdle() { /**/
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
                lastKnownLocation = Location("")
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
                        if (task.result != null) {
                            lastKnownLocation = task.result
                        } else {
                            lastKnownLocation.latitude = defaultLocation.latitude
                            lastKnownLocation.longitude = defaultLocation.longitude
                            marker = map?.addMarker(
                                MarkerOptions()
                                    .position(defaultLocation)
                                    .title("${defaultLocation.latitude},${defaultLocation.longitude} is the location")
                            )
                            requireView().snackbar("Location Service Not Enabled") {
                                startActivity(
                                    Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                    )
                                )
                            }
                        }
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation.latitude,
                                    lastKnownLocation.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        requireView().snackbar(task.exception.toString())
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        lastKnownLocation.latitude = defaultLocation.latitude
                        lastKnownLocation.longitude = defaultLocation.longitude
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
            viewModel.getWeather(
                lastKnownLocation.latitude.toString(),
                lastKnownLocation.longitude.toString(),
                apiKey
            )
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
    }
}