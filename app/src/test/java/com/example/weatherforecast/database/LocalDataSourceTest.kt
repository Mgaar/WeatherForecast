package com.example.weatherforecast.database

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
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
@MediumTest
class LocalDataSourceTest{

    private lateinit var localDataSource: LocalDataSource
    private lateinit var database: WeatherDataBase
    private lateinit var sharedPreferences: SharedPreferences

    @JvmField
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        localDataSource = LocalDataSource(database.getWeatherDAO(),sharedPreferences)
    }

    @After
    fun tearDown(){
        database.close()
        sharedPreferences.edit().clear().apply()
    }
@Test
    fun addFavCity_addSuccessfully():Unit= runTest {
        //given
        val favCity=FavCity(10.0,10.0,"test city","testCountry","testArabicCity","test")
   //when
        localDataSource.addFavCity(favCity)
    val favouritesList =  localDataSource.getAllCity().first()
    //then
    assertThat(favouritesList.size,IsEqual(1))
        }

    @Test
    fun addNotificatioCity_addSuccessfully():Unit= runTest {
        //given
        val notificationCity=NotificationCity(10.0,10.0,"test city","testCountry","testArabicCity","test","Time")
        //when
        localDataSource.addNotificatioCity(notificationCity)
        val notificationCityList =  localDataSource.getAllNotificationCity().first()
        //then
        assertThat(notificationCityList[0],IsEqual(notificationCity))
    }

}








