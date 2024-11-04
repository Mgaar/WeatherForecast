package com.example.weatherforecast.database

class FakeLocationDataSource :ILocationDataSource{
    override fun getLocation(onLocationReceived: (Double, Double) -> Unit) {
        TODO("Not yet implemented")
    }
}