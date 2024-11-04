package com.example.weatherforecast.database

import android.content.SharedPreferences
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getWeather(): Flow<WeatherDTO>
    fun getCurrentWeather(): Flow<CurrentWeather>

    suspend fun updateWeather(weather: WeatherDTO): Flow<Long>

    suspend fun updateCurrentWeather(weather: CurrentWeather): Flow<Long>
    fun getTempUnitPreferences(): String?
    fun getWindSpeedUnitPreferences(): String?
    fun getLanguagePreferences(): String?
    fun getLocationPreferences(): String?
    fun setHomeLocationMapPreferences(lat: Double, lon: Double)
    fun getMainLocationMapPreferencesLat(): Float
    fun getMainLocationMapPreferencesLon(): Float
    suspend fun addFavCity(favCity: FavCity): Long
    suspend fun removeFavCity(favCity: FavCity)
    fun getAllCity(): Flow<List<FavCity>>
    suspend fun addNotificatioCity(notificationCity: NotificationCity): Long
    suspend fun removeNotificationCity(notificationCity: NotificationCity)
    suspend fun removeNotificationCityById(id: String)
    fun getAllNotificationCity(): Flow<List<NotificationCity>>


}