package com.example.weatherforecast.model

import android.content.SharedPreferences
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository {
   private val remoteDataSource:RemoteDataSource
    private val localDataSource:LocalDataSource
    private val locationDataSource: LocationDataSource
    private constructor(remoteDataSource: RemoteDataSource,localDataSource:LocalDataSource,locationDataSource: LocationDataSource)  {
        this.remoteDataSource=remoteDataSource
        this.localDataSource = localDataSource
this.locationDataSource = locationDataSource
    }
    companion object{
        @Volatile
        private var repository:Repository? = null
        fun getInstance(remoteDataSource: RemoteDataSource,localDataSource:LocalDataSource,locationDataSource: LocationDataSource):Repository{
            return repository ?: synchronized(this){
                val temp =  Repository(remoteDataSource,localDataSource,locationDataSource)
                repository = temp
                temp
            }
        }
    }

    suspend fun getWeatherUpdate(lat:Double,lon:Double): Flow<WeatherDTO> =  remoteDataSource.getWeather(lat,lon)
    suspend fun getCurrentWeatherUpdate(lat:Double,lon:Double): Flow<CurrentWeather> =  remoteDataSource.getCurrentWeather(lat,lon)

     fun getWeatherCached():Flow<WeatherDTO> = localDataSource.getWeather()
    suspend fun updateCachedWeather(weatherDTO: WeatherDTO) :Flow<Long> = localDataSource.updateWeather(weatherDTO)


    fun getCurrentWeatherCached():Flow<CurrentWeather> = localDataSource.getCurrentWeather()
    suspend fun updateCurrentCachedWeather(weatherDTO: CurrentWeather) :Flow<Long> = localDataSource.updateCurrentWeather(weatherDTO)

    fun getTempUnitPreferences (): String? =  localDataSource.getTempUnitPreferences()
    fun getWindSpeedUnitPreferences (): String? =  localDataSource.getWindSpeedUnitPreferences()
    fun getLanguagePreferences (): String? =  localDataSource.getLanguagePreferences()
    fun getLocationPreferences (): String? =  localDataSource.getLocationPreferences()


    fun savePreferenceLocation (lat:Double,lon:Double){localDataSource.setHomeLocationMapPreferences(lat, lon)}
fun getMainLocationMapPreferencesLat():Float = localDataSource.getMainLocationMapPreferencesLat()
    fun getMainLocationMapPreferencesLon():Float=localDataSource.getMainLocationMapPreferencesLon()
    fun getGPSLocation(onLocationReceived: (Double, Double) -> Unit) = locationDataSource.getLocation(onLocationReceived)

    suspend  fun addFavCity(favCity: FavCity)=localDataSource.addFavCity(favCity)
suspend fun removeCity(favCity: FavCity)=localDataSource.removeFavCity(favCity)
    fun getAllFavCity()=localDataSource.getAllCity()

    suspend  fun addNotificatioCity(notificationCity: NotificationCity)=localDataSource.addNotificatioCity(notificationCity)
    suspend fun removeNotificationCity(notificationCity: NotificationCity)=localDataSource.removeNotificationCity(notificationCity)
  suspend fun removeNotificationCityById(id:String)=localDataSource.removeNotificationCityById(id)
    fun getAllNotificationCity()=localDataSource.getAllNotificationCity()
}