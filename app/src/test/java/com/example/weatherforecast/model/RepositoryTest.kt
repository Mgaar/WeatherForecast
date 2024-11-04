package com.example.weatherforecast.model
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.database.FakeLocalDataSource
import com.example.weatherforecast.database.FakeLocationDataSource
import com.example.weatherforecast.network.FakeRemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
private lateinit var fakeLocalDataSource: FakeLocalDataSource
private lateinit var fakeLocationDataSource: FakeLocationDataSource
private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
private lateinit var repository: IRepository
@Before
fun setUp(){
    fakeLocalDataSource=FakeLocalDataSource()
    fakeLocationDataSource=FakeLocationDataSource()
    fakeRemoteDataSource=FakeRemoteDataSource()
    repository = Repository(fakeRemoteDataSource,fakeLocalDataSource,fakeLocationDataSource)
}
@Test
fun addFavCity_returnAddedFavCity(): Unit = runTest {
//given
    val favCity =FavCity(10.0,10.0,"test city","testCountry","testArabicCity","test")
//when
    repository.addFavCity(favCity)
    var returnFavCity:FavCity = fakeLocalDataSource.favouritesList[0]

//then
    assertThat(returnFavCity,IsEqual(favCity) )
}
    @Test
    fun addTwoFavCity_RemoveFavCity_ReturnOneFav(): Unit = runTest {
//given
        val favCity =FavCity(10.0,10.0,"test city","testCountry","testArabicCity","test")
        val favCity2 =FavCity(10.0,10.0,"test city2","testCountry2","testArabicCity2","test2")

//when
        repository.addFavCity(favCity)
        repository.addFavCity(favCity2)
        repository.removeCity(favCity)

//then
        assertThat(fakeLocalDataSource.favouritesList.size, IsEqual(1))
    }
}