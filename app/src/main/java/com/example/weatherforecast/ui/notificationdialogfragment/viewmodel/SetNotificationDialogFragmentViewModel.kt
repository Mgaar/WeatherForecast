package com.example.weatherforecast.ui.notificationdialogfragment.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetNotificationDialogFragmentViewModel (val repo: Repository): ViewModel(){


    fun addNotificationCity(lat:Double,lon: Double,city:String,country:String,time:String, cityArabic:String, countryArabic:String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addNotificatioCity(NotificationCity(lat,lon,city, country,time,cityArabic,countryArabic))
        }

    }



}

class SetNotificationDialogFragmentViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(SetNotificationDialogFragmentViewModel::class.java)){
            SetNotificationDialogFragmentViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}