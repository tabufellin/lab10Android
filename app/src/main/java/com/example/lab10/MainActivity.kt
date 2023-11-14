package com.example.lab10

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lab10.databinding.ActivityMainBinding
import com.example.lab10.ui.theme.Lab10Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.security.Permission

class MainActivity : ComponentActivity() {


    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (allPermisssionsGrantedGPS()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            readCurrentLocation()

        } else {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID  )

        }

    }

    private fun allPermisssionsGrantedGPS() = Companion.REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun readCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result

                        if (location == null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                requestNewLocation()
                            }
                        } else {
                            binding.lblatitud.text = "LATITUD = " + location.latitude.toString()
                            binding.lblongitud.text = "LONGITUD = " + location.longitude.toString()

                        }

                    }
                }
            } else {
                Toast.makeText(this, "Activar Ubicacion", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                this.finish()

            }

        } else {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)

        }
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNewLocation() {
        var mLocationRequest = com.google.android.gms.location.LocationRequest()

        mLocationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())


        // mLocationRequest.priority
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager : LocationManager = getSystemService (Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val mLocationCallBack = object :  LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation : Location? = locationResult.lastLocation

            if (mLastLocation != null) {
                binding.lblatitud.text = "LATITUD = " + mLastLocation.latitude.toString()
                binding.lblongitud.text = "LONGITUD = " + mLastLocation.longitude.toString()

            }

            //super.onLocationResult(LocationResult)
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    companion object {
        private val REQUIRED_PERMISSIONS_GPS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab10Theme {
        Greeting("Android")
    }
}