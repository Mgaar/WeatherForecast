package com.example.weatherforecast.ui.map.viewmodel

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.IRepository
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow

class FakeRepository: IRepository {
var lat:Double=0.0
var lon:Double=0.0
 val favouritesList:MutableList<FavCity> = mutableListOf<FavCity>()

    override suspend fun addFavCity(favCity: FavCity): Long {
        favouritesList.add(favCity)
        return 1
    }

    override fun savePreferenceLocation(lat: Double, lon: Double) {
        this.lat = lat
        this.lon = lon
    }
    override fun getMainLocationMapPreferencesLat(): Float {
return this.lat.toFloat()
    }

    override fun getMainLocationMapPreferencesLon(): Float {
        return this.lon.toFloat()
    }
    override suspend fun getWeatherUpdate(lat: Double, lon: Double): Flow<WeatherDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherUpdate(lat: Double, lon: Double): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }

    override fun getWeatherCached(): Flow<WeatherDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCachedWeather(weatherDTO: WeatherDTO): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getCurrentWeatherCached(): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentCachedWeather(weatherDTO: CurrentWeather): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getTempUnitPreferences(): String? {
        TODO("Not yet implemented")
    }

    override fun getWindSpeedUnitPreferences(): String? {
        TODO("Not yet implemented")
    }

    override fun getLanguagePreferences(): String? {
        TODO("Not yet implemented")
    }

    override fun getLocationPreferences(): String? {
        TODO("Not yet implemented")
    }





    override fun getGPSLocation(onLocationReceived: (Double, Double) -> Unit) {
        TODO("Not yet implemented")
    }



    override suspend fun removeCity(favCity: FavCity) {
        TODO("Not yet implemented")
    }

    override fun getAllFavCity(): Flow<List<FavCity>> {
        TODO("Not yet implemented")
    }

    override suspend fun addNotificatioCity(notificationCity: NotificationCity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeNotificationCity(notificationCity: NotificationCity) {
        TODO("Not yet implemented")
    }

    override suspend fun removeNotificationCityById(id: String) {
        TODO("Not yet implemented")
    }

    override fun getAllNotificationCity(): Flow<List<NotificationCity>> {
        TODO("Not yet implemented")
    }
}