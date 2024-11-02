package com.example.weatherforecast.ui.notifications.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.CityDataState
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity
import com.example.weatherforecast.model.NotificationDataState
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class NotificationsViewModel (val repo: Repository): ViewModel() {
    private  var _postStateFlow: MutableStateFlow<NotificationDataState> = MutableStateFlow(NotificationDataState.Loading) //Observable -> emit
    var postStateFlow : StateFlow<NotificationDataState> = _postStateFlow

    fun getNotifications(){
        viewModelScope.launch(Dispatchers.IO){

            val flow = repo.getAllNotificationCity()
            flow.catch { e -> _postStateFlow.value= NotificationDataState.Failure(e) }
                .collect{ data -> _postStateFlow.value = NotificationDataState.Success(data)}
        }
    }

    fun removeNotification(notificationCity: NotificationCity){
        viewModelScope.launch(Dispatchers.IO) {
           repo.removeNotificationCity(notificationCity)
        }
    }
}


class NotificationsViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(NotificationsViewModel::class.java)){
            NotificationsViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}