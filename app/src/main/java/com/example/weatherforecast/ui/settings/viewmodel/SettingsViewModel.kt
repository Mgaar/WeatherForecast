package com.example.weatherforecast.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.Repository


class SettingsViewModel(val repo:Repository): ViewModel() {
    fun getTempUnit():String = repo.getTempUnitPreferences() ?: "0"
    fun getWindSpeedUnit():String = repo.getWindSpeedUnitPreferences() ?: "0"
    fun getLocationSource():String = repo.getLocationPreferences() ?: "0"
    fun getLanguage():String = repo.getLanguagePreferences() ?: "1"
}

class SettingsFragmentViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}