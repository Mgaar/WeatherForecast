package com.example.weatherforecast.ui.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherforecast.model.CityDataState
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel (val repo: Repository): ViewModel() {
    private  var _postStateFlow: MutableStateFlow<CityDataState> = MutableStateFlow(CityDataState.Loading) //Observable -> emit
    var postStateFlow : StateFlow<CityDataState> = _postStateFlow

    fun getFavCities(){
        viewModelScope.launch(Dispatchers.IO){

            val flow = repo.getAllFavCity()
            flow.catch { e -> _postStateFlow.value=CityDataState.Failure(e) }
                .collect{ data -> _postStateFlow.value =CityDataState.Success(data)}
        }
    }

    fun removeFavCity(favCity: FavCity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeCity(favCity)
        }
    }
}


class FavouriteFragmentViewModelFactory(val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if(modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            FavouriteViewModel(repo) as T
        }else {
            throw IllegalArgumentException("view model class not found")
        }
    }
}