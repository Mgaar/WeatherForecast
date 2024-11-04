package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getWeatherUpdate(lat: Double, lon: Double): Flow<WeatherDTO>

    suspend fun getCurrentWeatherUpdate(lat: Double, lon: Double): Flow<CurrentWeather>
    fun getWeatherCached(): Flow<WeatherDTO>

    suspend fun updateCachedWeather(weatherDTO: WeatherDTO): Flow<Long>
    fun getCurrentWeatherCached(): Flow<CurrentWeather>

    suspend fun updateCurrentCachedWeather(weatherDTO: CurrentWeather): Flow<Long>
    fun getTempUnitPreferences(): String?
    fun getWindSpeedUnitPreferences(): String?
    fun getLanguagePreferences(): String?
    fun getLocationPreferences(): String?
    fun savePreferenceLocation(lat: Double, lon: Double)
    fun getMainLocationMapPreferencesLat(): Float
    fun getMainLocationMapPreferencesLon(): Float
    fun getGPSLocation(onLocationReceived: (Double, Double) -> Unit)

    suspend fun addFavCity(favCity: FavCity): Long
    suspend fun removeCity(favCity: FavCity)
    fun getAllFavCity(): Flow<List<FavCity>>
    suspend fun addNotificatioCity(notificationCity: NotificationCity): Long
    suspend fun removeNotificationCity(notificationCity: NotificationCity)
    suspend fun removeNotificationCityById(id: String)
    fun getAllNotificationCity(): Flow<List<NotificationCity>>
}