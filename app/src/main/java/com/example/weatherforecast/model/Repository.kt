package com.example.weatherforecast.model

import android.content.SharedPreferences
import com.example.weatherforecast.database.ILocalDataSource
import com.example.weatherforecast.database.ILocationDataSource
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.network.IRemoteDataSource
import com.example.weatherforecast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource,
    private val locationDataSource: ILocationDataSource
) : IRepository {



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

    override suspend fun getWeatherUpdate(lat:Double, lon:Double): Flow<WeatherDTO> =  remoteDataSource.getWeather(lat,lon)
    override suspend fun getCurrentWeatherUpdate(lat:Double, lon:Double): Flow<CurrentWeather> =  remoteDataSource.getCurrentWeather(lat,lon)

     override fun getWeatherCached():Flow<WeatherDTO> = localDataSource.getWeather()
    override suspend fun updateCachedWeather(weatherDTO: WeatherDTO) :Flow<Long> = localDataSource.updateWeather(weatherDTO)


    override fun getCurrentWeatherCached():Flow<CurrentWeather> = localDataSource.getCurrentWeather()
    override suspend fun updateCurrentCachedWeather(weatherDTO: CurrentWeather) :Flow<Long> = localDataSource.updateCurrentWeather(weatherDTO)

    override fun getTempUnitPreferences (): String? =  localDataSource.getTempUnitPreferences()
    override fun getWindSpeedUnitPreferences (): String? =  localDataSource.getWindSpeedUnitPreferences()
    override fun getLanguagePreferences (): String? =  localDataSource.getLanguagePreferences()
    override fun getLocationPreferences (): String? =  localDataSource.getLocationPreferences()


    override fun savePreferenceLocation (lat:Double, lon:Double){localDataSource.setHomeLocationMapPreferences(lat, lon)}
override fun getMainLocationMapPreferencesLat():Float = localDataSource.getMainLocationMapPreferencesLat()
    override fun getMainLocationMapPreferencesLon():Float=localDataSource.getMainLocationMapPreferencesLon()
    override fun getGPSLocation(onLocationReceived: (Double, Double) -> Unit) = locationDataSource.getLocation(onLocationReceived)

    override suspend  fun addFavCity(favCity: FavCity)=localDataSource.addFavCity(favCity)
override suspend fun removeCity(favCity: FavCity)=localDataSource.removeFavCity(favCity)
    override fun getAllFavCity()=localDataSource.getAllCity()

    override suspend  fun addNotificatioCity(notificationCity: NotificationCity)=localDataSource.addNotificatioCity(notificationCity)
    override suspend fun removeNotificationCity(notificationCity: NotificationCity)=localDataSource.removeNotificationCity(notificationCity)
  override suspend fun removeNotificationCityById(id:String)=localDataSource.removeNotificationCityById(id)
    override fun getAllNotificationCity()=localDataSource.getAllNotificationCity()
}