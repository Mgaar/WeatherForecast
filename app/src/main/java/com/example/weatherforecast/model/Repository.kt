package com.example.weatherforecast.model

import android.content.SharedPreferences
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository {
   private val remoteDataSource:RemoteDataSource
    private val localDataSource:LocalDataSource

    private constructor(remoteDataSource: RemoteDataSource,localDataSource:LocalDataSource)  {
        this.remoteDataSource=remoteDataSource
        this.localDataSource = localDataSource

    }
    companion object{
        @Volatile
        private var repository:Repository? = null
        fun getInstance(remoteDataSource: RemoteDataSource,localDataSource:LocalDataSource):Repository{
            return repository ?: synchronized(this){
                val temp =  Repository(remoteDataSource,localDataSource)
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
}