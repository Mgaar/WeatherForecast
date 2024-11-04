package com.example.weatherforecast.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.IRepository
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(val repo: IRepository): ViewModel() {
    fun saveLocation(lat:Double,lon:Double){
       repo.savePreferenceLocation(lat, lon)
    }
    fun getLocationMapPreferencesLat():Double = repo.getMainLocationMapPreferencesLat().toDouble()
    fun getLocationMapPreferencesLon():Double = repo.getMainLocationMapPreferencesLon().toDouble()
    fun addFavCity(lat:Double,lon: Double,city:String,country:String, cityArabic:String, countryArabic:String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addFavCity(FavCity(lat,lon,city, country,cityArabic,countryArabic))
        }

    }

}

class MapViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}