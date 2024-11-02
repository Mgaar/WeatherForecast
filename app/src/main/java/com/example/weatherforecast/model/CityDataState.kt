package com.example.weatherforecast.model

sealed class CityDataState {
    class Success(val data: List<FavCity>) : CityDataState()
    class Failure(val msg: Throwable) : CityDataState()
    object Loading : CityDataState()
}

sealed class NotificationDataState {
    class Success(val data: List<NotificationCity>) : NotificationDataState()
    class Failure(val msg: Throwable) : NotificationDataState()
    object Loading : NotificationDataState()
}