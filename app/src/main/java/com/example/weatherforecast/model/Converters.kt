package com.example.weatherforecast.model
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
        private val gson = Gson()

        @TypeConverter
        fun fromWeatherDataList(value: List<WeatherData>?): String {
            return gson.toJson(value)
        }

        @TypeConverter
        fun toWeatherDataList(value: String): List<WeatherData>? {
            val listType = object : TypeToken<List<WeatherData>>() {}.type
            return gson.fromJson(value, listType)
        }

    @TypeConverter
    fun fromCity(value:City?): String {
        return gson.toJson(value)
    }
    @TypeConverter
    fun toCity(value: String):City? {
        val city = object : TypeToken<City>() {}.type
        return gson.fromJson(value, city)
    }

    @TypeConverter
    fun fromCoord(value: Coord): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCoord(value: String): Coord? {
        val listType = object : TypeToken<Coord>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromWeather(value: List<Weather>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeather(value: String): List<Weather>? {
        val listType = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMain(value: Main): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMain(value: String):Main? {
        val listType = object : TypeToken<Main>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromWind(value: Wind): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWind(value: String):Wind? {
        val listType = object : TypeToken<Wind>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromRain(value: Rain?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRain(value: String):Rain? {
        val listType = object : TypeToken<Rain>() {}.type
        return gson.fromJson(value, listType)
    }
    @TypeConverter
    fun fromClouds(value: Clouds): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toClouds(value: String):Clouds? {
        val listType = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMySys(value: MySys): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMySys(value: String):MySys? {
        val listType = object : TypeToken<MySys>() {}.type
        return gson.fromJson(value, listType)
    }
}