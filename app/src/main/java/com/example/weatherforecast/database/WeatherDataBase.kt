package com.example.weatherforecast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.model.Converters
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.WeatherDTO

@Database(entities = [WeatherDTO::class, CurrentWeather::class],version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase  : RoomDatabase() {
    abstract fun getWeatherDAO(): WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBase? = null
        fun getInstance(ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val temp = Room.databaseBuilder(
                    ctx.applicationContext,
                    WeatherDataBase::class.java, "weather_database"
                ).build()
                INSTANCE = temp
                temp
            }
        }
    }
}