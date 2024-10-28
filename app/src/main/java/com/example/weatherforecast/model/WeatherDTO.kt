package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    val id: Int,
    val name: String?,
    val coord: Coord?,
    val country: String?,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int
):Serializable

data class Clouds(
    val all: Int
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class WeatherData(
    val dt: Int,
    val main: Main,
    var weather: List<Weather>,
    val clouds: Clouds?,
    val wind: Wind?,
    val visibility: Int,
    val pop: Double,
    val sys: Sys?,
    val dt_txt: String,
    val rain: Rain?
):Serializable

data class Main(
    var temp: Double,
    val feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double

)

data class Rain(
    @SerializedName("3h")
    val _3h: Double
)



@Entity(tableName = "weather_table")
data class WeatherDTO(
    val cod: String?,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherData>,
    var hourList: List<WeatherData>,
    var dayList: List<WeatherData>,
    val city: City
    ,@PrimaryKey
    var id :Int = 1
):Serializable



data class Sys(
    val pod: String?
)

data class Weather(
    val id: Int,
    val main: String?,
    val description: String?,
    val icon: String?
)

data class Wind(
    var speed: Double,
    val deg: Int,
    val gust: Double
)