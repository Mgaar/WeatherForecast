package com.example.weatherforecast.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherDAOTest {
    private lateinit var database: WeatherDataBase
    private lateinit var weatherDao: WeatherDAO
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()



    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java).allowMainThreadQueries().build()

        weatherDao = database.getWeatherDAO()
    }

    @After
    fun tearDown(){
        database.close()
    }



    @Test
    fun addFavCity_addSuccessfully():Unit= runTest {
        //given
        val favCity= FavCity(10.0,10.0,"test city","testCountry","testArabicCity","test")
        //when
       weatherDao.addFavCity(favCity)
        val favouritesList =  weatherDao.getAllFav().first()
        //then
        assertThat(favouritesList.size, IsEqual(1))
    }

    @Test
    fun addNotificatioCity_addSuccessfully():Unit= runTest {
        //given
        val notificationCity=
            NotificationCity(10.0,10.0,"test city","testCountry","testArabicCity","test","Time")
        //when
        weatherDao.addNotificationCity(notificationCity)
        val notificationCityList =  weatherDao.getAllNotification().first()
        //then
        assertThat(notificationCityList[0], IsEqual(notificationCity))
    }

}