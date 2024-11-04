package com.example.weatherforecast.database

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource( val favouritesList:MutableList<FavCity> = mutableListOf<FavCity>()) :ILocalDataSource{

    override suspend fun addFavCity(favCity: FavCity): Long {
favouritesList.add(favCity)
    return 1
    }

    override suspend fun removeFavCity(favCity: FavCity) {
favouritesList.remove(favCity)
    }

    override fun getAllCity(): Flow<List<FavCity>> {
        return flow { emit(favouritesList) }
    }

    override fun getWeather(): Flow<WeatherDTO> {
        TODO("Not yet implemented")
    }

    override fun getCurrentWeather(): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeather(weather: WeatherDTO): Flow<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentWeather(weather: CurrentWeather): Flow<Long> {
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

    override fun setHomeLocationMapPreferences(lat: Double, lon: Double) {
        TODO("Not yet implemented")
    }

    override fun getMainLocationMapPreferencesLat(): Float {
        TODO("Not yet implemented")
    }

    override fun getMainLocationMapPreferencesLon(): Float {
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