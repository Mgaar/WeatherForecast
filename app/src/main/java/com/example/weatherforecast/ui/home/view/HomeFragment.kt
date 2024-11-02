package com.example.weatherforecast.ui.home.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.DataState
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weatherforecast.ui.home.viewmodel.HomeViewModel
import com.example.weatherforecast.ui.settings.viewmodel.SettingsFragmentViewModelFactory
import com.example.weatherforecast.ui.settings.viewmodel.SettingsViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


class HomeFragment : Fragment() {
    val URL = "https://openweathermap.org/img/wn/"

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeFragmentViewModelFactory: HomeFragmentViewModelFactory
    private lateinit var viewModel: HomeViewModel
    private lateinit var settingsFragmentViewModelFactory: SettingsFragmentViewModelFactory
    private lateinit var settingsViewModel: SettingsViewModel
    lateinit var hourListAdapter: HomeFragmentHourlyListAdapter
    lateinit var hourLayoutManager: LinearLayoutManager

    lateinit var dayListAdapter: HomeFragmentDailyListAdapter
    lateinit var dayLayoutManager: LinearLayoutManager

    lateinit var numberFormat: NumberFormat

    lateinit var tempUnitPref:String
    lateinit var windSpeedPref:String
    lateinit var languagePref:String
    lateinit var locationPref:String

    var lat:Float = 30.071184f
     var lon:Float = 31.021251f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)


        settingsFragmentViewModelFactory = SettingsFragmentViewModelFactory(Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(RetrofitHelper)
            ,LocalDataSource.getInstance(WeatherDataBase.getInstance(this.requireContext()).getWeatherDAO()
                ,PreferenceManager.getDefaultSharedPreferences(this.context as Context)
            ),LocationDataSource.getInstance(LocationServices.getFusedLocationProviderClient(requireActivity()))
             ))

        settingsViewModel = ViewModelProvider(this,settingsFragmentViewModelFactory).get(SettingsViewModel::class.java)


        tempUnitPref = settingsViewModel.getTempUnit()
        windSpeedPref = settingsViewModel.getWindSpeedUnit()
        languagePref = settingsViewModel.getLanguage()
        locationPref = settingsViewModel.getLocationSource()



        numberFormat = NumberFormat.getInstance()

        homeFragmentViewModelFactory = HomeFragmentViewModelFactory(Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(RetrofitHelper)
            ,LocalDataSource.getInstance(WeatherDataBase.getInstance(this.requireContext()).getWeatherDAO()
                ,PreferenceManager.getDefaultSharedPreferences(this.context as Context)),
            LocationDataSource.getInstance(LocationServices.getFusedLocationProviderClient(requireActivity())
        )))

        viewModel=ViewModelProvider(this,homeFragmentViewModelFactory).get(HomeViewModel::class.java)



       hourListAdapter = HomeFragmentHourlyListAdapter(tempUnitPref)
        hourLayoutManager = LinearLayoutManager(this.context)
        hourLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.homeFragmentHourlyRecyclerView.apply {
            adapter = hourListAdapter
            layoutManager = hourLayoutManager
        }

       dayListAdapter = HomeFragmentDailyListAdapter()
        dayLayoutManager = LinearLayoutManager(this.context)
        dayLayoutManager.orientation = RecyclerView.VERTICAL
        binding.homeFragmentDailyRecyclerView.apply {
            adapter = dayListAdapter
            layoutManager = dayLayoutManager
        }

        if (arguments?.getString("source")?:"home" == "fav")
        {
            var lat = arguments?.getDouble("lat") as Double
            var lon = arguments?.getDouble("lon") as Double
            viewModel.getForecastWeather(true,lat,lon,tempUnitPref,windSpeedPref)
            viewModel.getCurrentWeather(true,lat,lon,tempUnitPref,windSpeedPref)
        }
        else {
            viewModel.getWeather(true,tempUnitPref,windSpeedPref)

        }

        when (tempUnitPref){
            "1" -> {binding.homeFragUnitTextView.text = getString(R.string.fahrenheit)}
            "2" ->{binding.homeFragUnitTextView.text = getString(R.string.kelvin)}
            else -> {binding.homeFragUnitTextView.text = getString(R.string.celsius)}
        }

        if (windSpeedPref == "1")
            binding.homeFragmentWindSpeedUnitCardViewTextView.text = getString(R.string.mileperhou)
        else
            binding.homeFragmentWindSpeedUnitCardViewTextView.text = getString(R.string.meterpersec)

        lifecycleScope.launch { viewModel.postWeatherStateFlow.collectLatest {
            when(it){
                is DataState.Success -> {
                    dayListAdapter.submitList(it.data.dayList)
                    hourListAdapter.submitList(it.data.hourList)
                }
                is DataState.Failure -> {}
                else ->{} //do something

            }
        } }
        lifecycleScope.launch { viewModel.postCurrentWeatherStateFlow.collectLatest {
            when(it){
                is DataState.SuccessCurrent -> {
                    binding.homeFragmentCityTextView.text=it.data.name
                    binding.homeFragmentWeatherDescriptionTextView.text = it.data.weather.get(0).description
                    binding.homeFragmentMainTempDegree.text =numberFormat.format(it.data.main.temp.toInt())
                    val date = convertUtcToLocalTime(it.data.dt.toLong(),it.data.timezone)
                    Log.i("TAG", "onViewCreated: "+date)
                    binding.homeFragTodayTextView.text = date
                    binding.homeFragmentCurrentTime.text = " "
                    val sunrise = getSunTime(it.data.sys.sunrise.toLong(),it.data.timezone)
                binding.homeFragmentSunriseTextView.text = sunrise
                    val sunset = getSunTime(it.data.sys.sunset.toLong(),it.data.timezone)
                    binding.homeFragmentSunsetTextView.text = sunset
                    binding.homeFragmentPressureTextView.text = numberFormat.format(it.data.main.pressure)
                    binding.homeFragmentWindSpeedTextView.text = numberFormat.format(it.data.wind.speed)
                    binding.homeFragmentCloudsTextView.text = numberFormat.format(it.data.clouds.all)
                    binding.homeFragmentHumidityTextView.text = numberFormat.format(it.data.main.humidity)
                    this@HomeFragment?.context?.let { it1 ->
                        Glide.with(it1).load(URL + it.data.weather.get(0).icon +"@2x.png").apply(
                            RequestOptions().override(150, 150)).into( binding.homeFragmentCardViewWeatherIcon)
                    }


                }
                is DataState.Failure -> {}
                else ->{} //do something

            }
        } }

    }
    fun convertUtcToLocalTime(utcTimeInSeconds: Long, utcOffsetSeconds: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = utcTimeInSeconds * 1000

        calendar.add(Calendar.MILLISECOND, utcOffsetSeconds*1000)

        val dateFormat = SimpleDateFormat("EEE, dd MMM hh:mm a")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(calendar.time)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TAG", "onDestroyView: ")
    }


}

fun getSunTime(utcTimeInSeconds: Long, utcOffsetSeconds: Int): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = utcTimeInSeconds * 1000

    calendar.add(Calendar.MILLISECOND, utcOffsetSeconds*1000)

    val dateFormat = SimpleDateFormat("hh:mm a")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    return dateFormat.format(calendar.time)
}