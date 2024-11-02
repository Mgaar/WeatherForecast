package com.example.weatherforecast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
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

    @Query("SELECT * FROM favcity_table ")
    fun getAllFav(): Flow<List<FavCity>>

    @Query("SELECT * FROM favcity_table WHERE city = :cityStr")
    fun getFavById(cityStr: String): Flow<FavCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavCity (favCity: FavCity):Long

    @Delete
    suspend fun removeFavCity (favCity: FavCity)

    @Query("SELECT * FROM notification_table ")
    fun getAllNotification(): Flow<List<NotificationCity>>

    @Query("SELECT * FROM notification_table WHERE city = :cityStr")
    fun getNotificationById(cityStr: String): Flow<NotificationCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotificationCity (notificationCity: NotificationCity):Long

    @Delete
    suspend fun removeNotificationCity (notificationCity: NotificationCity)
    @Query("DELETE FROM notification_table WHERE city = :notificationId")
    suspend fun removeNotificationCityById(notificationId: String)
}





