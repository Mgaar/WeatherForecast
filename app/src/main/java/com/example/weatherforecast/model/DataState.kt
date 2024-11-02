package com.example.weatherforecast.model

sealed class DataState {
    class Success(val data: WeatherDTO) : DataState()
    class SuccessCurrent(val data: CurrentWeather) : DataState()
    class Failure(val msg: Throwable) : DataState()
    object Loading : DataState()
}

