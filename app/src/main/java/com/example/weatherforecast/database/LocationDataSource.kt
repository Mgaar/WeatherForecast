package com.example.weatherforecast.database

import android.annotation.SuppressLint



import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class LocationDataSource : ILocationDataSource {

    private val fusedLocationProviderClient: FusedLocationProviderClient

    private constructor(fusedLocationProviderClient: FusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient
    }

    companion object {
        @Volatile
        private var locationDataSource: LocationDataSource? = null
         fun getInstance(fusedLocationProviderClient: FusedLocationProviderClient): LocationDataSource {
            return locationDataSource ?: synchronized(this) {
                val temp = LocationDataSource(fusedLocationProviderClient)
                locationDataSource = temp
                temp
            }
        }
    }
        @SuppressLint("MissingPermission")
        override fun getLocation(onLocationReceived: (Double, Double) -> Unit) {
            val locationRequest = LocationRequest.Builder(1)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()


          fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                    val   lat = locationResult.lastLocation?.latitude ?: 0.0
                      val   lon = locationResult.lastLocation?.longitude ?: 0.0
                        onLocationReceived(lat,lon)
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                },
                Looper.myLooper()
            )


        }
    }
