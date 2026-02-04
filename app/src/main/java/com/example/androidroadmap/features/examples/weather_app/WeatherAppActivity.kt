package com.example.androidroadmap.features.examples.weather_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.androidroadmap.features.examples.weather_app.ui.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherAppActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.any { it.value }
            viewModel.onLocationPermissionResult(granted)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationIfNeeded()
        setContent {
            WeatherScreen()
        }
    }

    private fun requestLocationIfNeeded() {
        if (!isLocationEnabled()) {
            showEnableLocationDialog()
            requestLocationIfNeeded()
        }

        if (hasLocationPermission()) {
            viewModel.onLocationPermissionResult(true)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    private fun ComponentActivity.hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
    private fun showEnableLocationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Location services are disabled. Please enable GPS or network location to use this feature.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                startActivity(Intent(intent))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}