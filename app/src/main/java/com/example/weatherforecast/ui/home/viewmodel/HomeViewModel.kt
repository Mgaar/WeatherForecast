package com.example.weatherforecast.ui.home.viewmodel
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.DataState
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.WeatherDTO
import com.example.weatherforecast.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(val repo: Repository) : ViewModel() {

    private var lat:Double = 0.0
    private var lon:Double = 0.0

    private  var _postWeatherStateFlow: MutableStateFlow<DataState> = MutableStateFlow(DataState.Loading) //Observable -> emit
    var postWeatherStateFlow : StateFlow<DataState> = _postWeatherStateFlow

    private  var _postCurrentWeatherStateFlow: MutableStateFlow<DataState> = MutableStateFlow(DataState.Loading) //Observable -> emit
    var postCurrentWeatherStateFlow : StateFlow<DataState> = _postCurrentWeatherStateFlow

    fun getMainLocationMapPreferencesLat():Float = repo.getMainLocationMapPreferencesLat()
    fun getMainLocationMapPreferencesLon():Float = repo.getMainLocationMapPreferencesLon()

    fun getGPSLocation(onLocationReceived: (Double, Double) -> Unit) = repo.getGPSLocation(onLocationReceived)

    fun getWeather(isNetworkAvailable: Boolean,tempUnit:String,windSpeedUnit:String,geocoder: Geocoder) {
       if(repo.getLocationPreferences() == "0")
       {
          getGPSLocation{lat,lon->
              this@HomeViewModel.lat = lat
              this@HomeViewModel.lon = lon
              getForecastWeather(isNetworkAvailable,lat,lon,tempUnit,windSpeedUnit)
              getCurrentWeather(isNetworkAvailable,lat,lon,tempUnit,windSpeedUnit,geocoder)

          }
       }
       else {
lat = getMainLocationMapPreferencesLat().toDouble()
           lon = getMainLocationMapPreferencesLon().toDouble()
           getForecastWeather(isNetworkAvailable,lat,lon,tempUnit,windSpeedUnit)
           getCurrentWeather(isNetworkAvailable,lat,lon,tempUnit,windSpeedUnit,geocoder)

       }
    }

    fun getForecastWeather(isNetworkAvailable: Boolean,lat:Double,lon:Double,tempUnit:String,windSpeedUnit:String){
        _postWeatherStateFlow.value = DataState.Loading
        if (isNetworkAvailable) {
            viewModelScope.launch (Dispatchers.IO){
                val flow = repo.getWeatherUpdate(lat,lon)
                flow.catch { e -> _postWeatherStateFlow.value=DataState.Failure(e) }
                    .collect{ data ->
                        data.id = 1
                        Log.i("TAG", "getForecastWeathercache: "+data.toString())
                        HomeViewModel@parseWeatherList(data)
                        repo.updateCachedWeather(data)
                        editTempUnitWeatherDTO(tempUnit,data)
                        _postWeatherStateFlow.value =DataState.Success(data)
                         }
            }
        }
        else
        {
            viewModelScope.launch (Dispatchers.IO){
                val flow = repo.getWeatherCached()
                flow.catch { e -> _postWeatherStateFlow.value=DataState.Failure(e) }
                    .collect{ data ->
                    //    HomeViewModel@parseWeatherList(data)
                        editTempUnitWeatherDTO(tempUnit,data)
                        _postWeatherStateFlow.value =DataState.Success(data) }
            }
        }
    }
    fun getCurrentWeather(isNetworkAvailable: Boolean,lat:Double,lon:Double,tempUnit:String,windSpeedUnit:String,geocoder: Geocoder) {
        _postCurrentWeatherStateFlow.value = DataState.Loading
        if (isNetworkAvailable) {
            viewModelScope.launch (Dispatchers.IO){
                var address =  geocoder.getFromLocation(lat,lon,1)
                val flow = repo.getCurrentWeatherUpdate(lat,lon)
                flow.catch { e -> _postCurrentWeatherStateFlow.value=DataState.Failure(e) }
                    .collect{ data ->
                        data.id=1
                        data.nameArabic = address?.get(0)?.adminArea.toString()
                        repo.updateCurrentCachedWeather(data)
                        editTempUnitCurrentWeather(windSpeedUnit,tempUnit,data)
                        _postCurrentWeatherStateFlow.value =DataState.SuccessCurrent(data)
                         }
            }
        }
        else
        {
            viewModelScope.launch (Dispatchers.IO){
                val flow = repo.getCurrentWeatherCached()
                flow.catch { e -> _postCurrentWeatherStateFlow.value=DataState.Failure(e) }
                    .collect{ data ->
                        editTempUnitCurrentWeather(windSpeedUnit,tempUnit,data)
                        _postCurrentWeatherStateFlow.value =DataState.SuccessCurrent(data) }
            }
        }
    }


    private fun parseWeatherList (weatherDto:WeatherDTO){
        var day :MutableList<WeatherData> = mutableListOf()
      val  today = weatherDto.list?.get(0)?.dt_txt?.substringBefore(" ")
        for (i in weatherDto.list ?: emptyList() )
        {
if (i.dt_txt?.substringBefore(" ") == today)
    day.add(i)
            else
                break
        }
        weatherDto.hourList = day
        weatherDto.dayList = weatherDto?.list?.minus(weatherDto.hourList) ?: emptyList()
        filterDays(weatherDto)

    }

    fun filterDays(weatherDto:WeatherDTO){
        var filteredList :MutableList<WeatherData> = mutableListOf()
        for (i in weatherDto.dayList.indices step 8)
        {
            if (i+4 >=weatherDto.dayList.lastIndex   )
                break

            weatherDto.dayList.get(i).main?.temp_max =  weatherDto.dayList.get(i+4).main.temp_max
            weatherDto.dayList.get(i).main?.temp_min =  weatherDto.dayList.get(i+1).main.temp_min
            weatherDto.dayList.get(i).weather =  weatherDto.dayList.get(i+4).weather

            filteredList.add(weatherDto.dayList.get(i))
        }
        weatherDto.dayList = filteredList
    }
    fun editTempUnitWeatherDTO(tempUnit: String, weatherDto: WeatherDTO){
        if (tempUnit == "1")
        {
            //(temp*2 )+ 30
            for (i in weatherDto.list)
            {
                i.main.temp_max = ( i.main.temp_max * 2) + 30
                i.main.temp_min = ( i.main.temp_min * 2) + 30
                i.main.temp = ( i.main.temp * 2) + 30
            }
        }
        else if (tempUnit == "2" ) {
            //temp+273.15.
            for (i in weatherDto.list) {
                i.main.temp_max = i.main.temp_max + 273.15
                i.main.temp_min = i.main.temp_min + 273.15
                i.main.temp = i.main.temp + 273.15
            }
        }
        else {
        //do nothing
         }
    }
    fun editTempUnitCurrentWeather(windSpeedUnit:String,tempUnit: String, weather: CurrentWeather){
        if (tempUnit == "1")
        {
            weather.main.temp = (weather.main.temp * 2) + 30
        }
        else if (tempUnit == "2")
        {
            weather.main.temp = (weather.main.temp + 273.15)
        }
        if (windSpeedUnit == "1") {
        weather.wind.speed = weather.wind.speed * 2.237
        }

    }

}



class HomeFragmentViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}
