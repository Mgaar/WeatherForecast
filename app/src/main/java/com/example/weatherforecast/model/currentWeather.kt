package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

class MySys {
        var type: Int = 0
        var id: Int = 0
        var country: String? = null
        var sunrise: Int = 0
        var sunset: Int = 0
}

@Entity(tableName = "currentweather_table")
data class CurrentWeather (
        var coord: Coord,
        var weather: List<Weather>,
        var base: String,
        var main: Main,
        var wind: Wind,
        var rain: Rain ?=null,
        var clouds: Clouds,
        var dt: Int,
        var sys: MySys,
        var timezone: Int ,
        @PrimaryKey
        var id: Int=1,
        var name: String,
        var nameArabic: String,
        var cod: Int,

):Serializable