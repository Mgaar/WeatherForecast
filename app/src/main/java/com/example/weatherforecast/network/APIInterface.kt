package com.example.weatherforecast.network

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast")
    suspend fun getForecast(@Query("lat") latitude:Double,
                            @Query("lon") longitude:Double,
                            @Query("appid") apiKey:String,
                            @Query("units") unit:String): WeatherDTO

    @GET("weather")
    suspend fun getCurrentWeather(@Query("lat") latitude:Double,
                            @Query("lon") longitude:Double,
                            @Query("appid") apiKey:String,
                            @Query("units") unit:String): CurrentWeather
}