package com.example.weatherforecast.database

import android.content.SharedPreferences
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSource(private var weatherDAO:WeatherDAO,
                      private val sharedPreferences: SharedPreferences
) : ILocalDataSource {
    companion object{
        @Volatile
        private var localDataSource:LocalDataSource ?= null
         fun getInstance(weatherDAO:WeatherDAO, sharedPreferences:SharedPreferences):LocalDataSource{
            return localDataSource ?: synchronized(this){
                val temp =  LocalDataSource(weatherDAO,sharedPreferences)
                localDataSource = temp
                temp
            }
        }
    }
    override fun getWeather(): Flow<WeatherDTO> = weatherDAO.getWeatherByCityId()
    override fun getCurrentWeather(): Flow<CurrentWeather> = weatherDAO.getCurrentWeatherByCityId()

    override suspend fun updateWeather(weather:WeatherDTO):Flow<Long> {
         var result =  weatherDAO.updateWeather(weather)
        return flow<Long> { emit(result) }
    }
    override suspend fun updateCurrentWeather(weather: CurrentWeather):Flow<Long> {
        var result =  weatherDAO.updateCurrentWeather(weather)
        return flow<Long> { emit(result) }
    }

    override fun getTempUnitPreferences (): String? =  sharedPreferences.getString("list_preference_2", "0")
    override fun getWindSpeedUnitPreferences (): String? =  sharedPreferences.getString("list_preference_3", "0")
    override fun getLanguagePreferences (): String? =  sharedPreferences.getString("list_preference_1", "1")
    override fun getLocationPreferences (): String? =  sharedPreferences.getString("list_preference_4", "0")

override fun setHomeLocationMapPreferences(lat:Double, lon:Double){
    with(sharedPreferences.edit()) {
        putFloat("mainlat", lat.toFloat())
        putFloat("mainlon", lon.toFloat())
        apply() // Use apply to save changes asynchronously
    }
}
    override fun getMainLocationMapPreferencesLat():Float= sharedPreferences.getFloat("mainlat", 0.0f)
    override fun getMainLocationMapPreferencesLon():Float= sharedPreferences.getFloat("mainlon", 0.0f)

       override suspend fun addFavCity (favCity: FavCity)=weatherDAO.addFavCity(favCity)
    override suspend fun removeFavCity(favCity: FavCity)=weatherDAO.removeFavCity(favCity)
    override fun getAllCity()=weatherDAO.getAllFav()

    override suspend fun addNotificatioCity (notificationCity: NotificationCity)=weatherDAO.addNotificationCity(notificationCity)
    override suspend fun removeNotificationCity(notificationCity: NotificationCity)=weatherDAO.removeNotificationCity(notificationCity)
   override suspend fun removeNotificationCityById(id:String)=weatherDAO.removeNotificationCityById(id)
    override fun getAllNotificationCity()=weatherDAO.getAllNotification()
}