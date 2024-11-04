package com.example.weatherforecast.network

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow

class FakeRemoteDataSource:IRemoteDataSource {
    override suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }
}