package com.example.weatherforecast.database

import android.content.SharedPreferences
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSource {


    private var weatherDAO:WeatherDAO
    private val sharedPreferences:SharedPreferences

    private constructor(weatherDAO:WeatherDAO,sharedPreferences:SharedPreferences)  {
        this.weatherDAO = weatherDAO
        this.sharedPreferences = sharedPreferences
    }

    companion object{
        @Volatile
        private var localDataSource:LocalDataSource ?= null
        fun getInstance(weatherDAO:WeatherDAO,sharedPreferences:SharedPreferences):LocalDataSource{
            return localDataSource ?: synchronized(this){
                val temp =  LocalDataSource(weatherDAO,sharedPreferences)
                localDataSource = temp
                temp
            }
        }
    }
    fun getWeather(): Flow<WeatherDTO> = weatherDAO.getWeatherByCityId()
    fun getCurrentWeather(): Flow<CurrentWeather> = weatherDAO.getCurrentWeatherByCityId()

    suspend fun updateWeather(weather:WeatherDTO):Flow<Long> {
         var result =  weatherDAO.updateWeather(weather)
        return flow<Long> { emit(result) }
    }
    suspend fun updateCurrentWeather(weather: CurrentWeather):Flow<Long> {
        var result =  weatherDAO.updateCurrentWeather(weather)
        return flow<Long> { emit(result) }
    }

    fun getTempUnitPreferences (): String? =  sharedPreferences.getString("list_preference_2", "0")
    fun getWindSpeedUnitPreferences (): String? =  sharedPreferences.getString("list_preference_3", "0")
    fun getLanguagePreferences (): String? =  sharedPreferences.getString("list_preference_1", "1")
    fun getLocationPreferences (): String? =  sharedPreferences.getString("list_preference_4", "0")

fun setHomeLocationMapPreferences(lat:Double,lon:Double){
    with(sharedPreferences.edit()) {
        putFloat("mainlat", lat.toFloat())
        putFloat("mainlon", lon.toFloat())
        apply() // Use apply to save changes asynchronously
    }
}
    fun getMainLocationMapPreferencesLat():Float= sharedPreferences.getFloat("mainlat", 0.0f)
    fun getMainLocationMapPreferencesLon():Float= sharedPreferences.getFloat("mainlon", 0.0f)

       suspend fun addFavCity (favCity: FavCity)=weatherDAO.addFavCity(favCity)
    suspend fun removeFavCity(favCity: FavCity)=weatherDAO.removeFavCity(favCity)
    fun getAllCity()=weatherDAO.getAllFav()

    suspend fun addNotificatioCity (notificationCity: NotificationCity)=weatherDAO.addNotificationCity(notificationCity)
    suspend fun removeNotificationCity(notificationCity: NotificationCity)=weatherDAO.removeNotificationCity(notificationCity)
   suspend fun removeNotificationCityById(id:String)=weatherDAO.removeNotificationCityById(id)
    fun getAllNotificationCity()=weatherDAO.getAllNotification()
}