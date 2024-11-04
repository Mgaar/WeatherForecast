package com.example.weatherforecast.network

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherDTO>

    suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeather>

}