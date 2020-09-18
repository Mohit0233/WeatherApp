package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainUiActivity : AppCompatActivity() {

    private lateinit var mainUiViewModel: MainUiViewModel
    private val locationPermissionGranted = MutableLiveData<Boolean>().apply {
        value = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ui)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        mainUiViewModel =
            ViewModelProvider(this).get(MainUiViewModel::class.java)

        mainUiViewModel.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
        locationPermissionGranted.observe(this, {
            mainUiViewModel.getDeviceLocation(locationPermissionGranted)
            //getDeviceLocation(locationPermissionGranted)
        })


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /*lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var defaultLocation: LatLng = LatLng(-33.8523341, 151.2106085)
    private var lastKnownLocation: Location? = null

    fun getDeviceLocation(locationPermissionGranted: MutableLiveData<Boolean>) {
        try {
            if (locationPermissionGranted.value!!) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    Log.e("task",task.toString())
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        Log.e("task", task.toString() + task.result)
                        if (lastKnownLocation != null) {
                            defaultLocation =
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                )
                        }
                    }
                }
            } else {
                defaultLocation = LatLng(33.1, 151.2)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
        Log.e("defaultLocaiton", defaultLocation.toString())
    }
*/

    override fun onResume() {
        super.onResume()

        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()
        getLocationPermission()

    }
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted.value = true
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
        locationPermissionGranted.value = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted.value = true
                }
            }
        }
    }

    companion object {
        //private val TAG = MainUiActivity::class.java.simpleName
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        //private const val KEY_LOCATION = "location"
    }

}