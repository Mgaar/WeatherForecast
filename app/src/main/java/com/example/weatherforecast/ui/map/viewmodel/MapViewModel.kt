package com.example.weatherforecast.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.Repository

class MapViewModel(val repo:Repository): ViewModel() {
    fun saveLocation(lat:Double,lon:Double){
       repo.savePreferenceLocation(lat, lon)
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