package com.example.weatherforecast.ui.map.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    lateinit var repository: FakeRepository
    lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp(){
        repository = FakeRepository()
        mapViewModel = MapViewModel(repository)
    }
    @Test
    fun saveLocation_UpdateLatLon() {
        //given
        val   myLat :Double = 23.5
        val   myLon :Double = 55.160
        //when
        mapViewModel.saveLocation(myLat,myLon)
        //then
        assertThat(myLat,IsEqual(repository.lat))
        assertThat(myLon,IsEqual(repository.lon))

    }
    @Test
    fun getLocationMapPreferencesLat_setLatandGetSameLat() {
//given
    val   myLat :Double = 23.5
    //when
    repository.lat=myLat
    var Lat = mapViewModel.getLocationMapPreferencesLat()
    //Then
    assertThat(myLat,IsEqual(Lat))


}
@Test
fun getLocationMapPreferencesLon_setDoubleLonandGetSameLonConvertedToFloat() {

        //given
        val   myLon :Double = 23.5
        //when
        repository.lon=myLon
        var Lon = mapViewModel.getLocationMapPreferencesLon()
        //Then
        assertThat(myLon,IsEqual(Lon))

    }

}