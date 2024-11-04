package com.example.weatherforecast.database

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient

interface ILocationDataSource {
    @SuppressLint("MissingPermission")
    fun getLocation(onLocationReceived: (Double, Double) -> Unit)


}