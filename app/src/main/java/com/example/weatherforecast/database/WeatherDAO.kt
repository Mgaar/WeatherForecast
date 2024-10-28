package com.example.weatherforecast.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weather_table WHERE id = 2")
     fun getWeatherByCityId(): Flow<WeatherDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWeather (weatherDTO: WeatherDTO):Long


    @Query("SELECT * FROM currentweather_table WHERE id =1")
    fun getCurrentWeatherByCityId(): Flow<CurrentWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrentWeather (currentWeather: CurrentWeather):Long

}





