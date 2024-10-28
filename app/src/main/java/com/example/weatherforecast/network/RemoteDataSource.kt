package com.example.weatherforecast.network

import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



class RemoteDataSource {
    private  var retrofitHelper:RetrofitHelper
private val  APIKEY = "6ac4343b1c33f6529cf970d8e5fbc9dc"
    private val  UNIT = "metric"

    private constructor(retrofitHelper:RetrofitHelper)  {
        this.retrofitHelper=retrofitHelper
    }

    companion object{
        @Volatile
        private var remoteDataSource:RemoteDataSource? = null
        fun getInstance(retrofitHelper:RetrofitHelper):RemoteDataSource{
            return remoteDataSource ?: synchronized(this){
                val temp =  RemoteDataSource(retrofitHelper)
                remoteDataSource = temp
                temp
            }
        }
    }

    suspend fun getWeather(lat:Double,lon:Double): Flow<WeatherDTO>
    {
        return flow { emit(retrofitHelper.service.getForecast(lat,lon,APIKEY,UNIT)) }
    }


    suspend fun getCurrentWeather(lat:Double,lon:Double): Flow<CurrentWeather>
    {
        return flow { emit(retrofitHelper.service.getCurrentWeather(lat,lon,APIKEY,UNIT)) }
    }

}