package com.example.weatherforecast.ui.map.view


import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherforecast.R
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.databinding.ActivityMapBinding
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.isOnline
import com.example.weatherforecast.ui.map.viewmodel.MapViewModel
import com.example.weatherforecast.ui.map.viewmodel.MapViewModelFactory
import com.google.android.gms.location.LocationServices
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MapActivity : AppCompatActivity() {
    lateinit var mapView: MapView
    private lateinit var binding: ActivityMapBinding
 lateinit var mapViewModel: MapViewModel
 lateinit var mapViewModelFactory: MapViewModelFactory
 lateinit var geocoder: Geocoder
 lateinit var source:String
    var lat:Double = 0.0
    var lon:Double = 0.0
lateinit var englishCity:String
    lateinit var englishCountry:String

    lateinit var arabicCity:String
    lateinit var arabicCountry:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = intent.getStringExtra("source")?:"null"



        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapReloadButton.setOnClickListener{
            mapNetwork()
        }

        mapNetwork()
        binding.mapActCard.visibility = View.GONE
        mapViewModelFactory = MapViewModelFactory(Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(
            RetrofitHelper), LocalDataSource.getInstance(
                WeatherDataBase.getInstance(this).getWeatherDAO()
                , PreferenceManager.getDefaultSharedPreferences(this )), LocationDataSource.getInstance(
            LocationServices.getFusedLocationProviderClient(this)
        )))
        mapViewModel = ViewModelProvider(this,mapViewModelFactory).get(MapViewModel::class.java)


        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        val startPoint: GeoPoint = GeoPoint(mapViewModel.getLocationMapPreferencesLat(), mapViewModel.getLocationMapPreferencesLon())
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(startPoint)


        // Add a marker
        val marker: Marker = Marker(mapView)
        marker.setPosition(startPoint)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        // Set up MapEventsOverlay for detecting touch events
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                mapNetwork()
                marker.position = geoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                // Display the latitude and longitude
                lat = geoPoint.latitude
                lon = geoPoint.longitude
                Log.i("TAG", "singleTapConfirmedHelper: ")

                    geocoder = Geocoder(this@MapActivity,Locale("en"))
                val geocoderArabic = Geocoder(this@MapActivity, Locale("ar"))
if (isOnline(this@MapActivity))
{
    var address =  geocoder.getFromLocation(lat,lon,1)
    var arabicAddress = geocoderArabic.getFromLocation(lat,lon,1)
    binding.mapActCard.visibility = View.VISIBLE
    if (!address.isNullOrEmpty())
    {
        val currentLocale = Locale.getDefault()
        englishCity = address?.get(0)?.adminArea.toString()
        englishCountry = address?.get(0)?.countryName.toString()
        arabicCountry =arabicAddress?.get(0)?.countryName.toString()
        arabicCity = arabicAddress?.get(0)?.adminArea.toString()
        if (currentLocale.language == "ar")
        {
            binding.mapActCityTextView.text=arabicAddress?.get(0)?.adminArea/*?.substringBefore(",")*/
            binding.mapActCountryTextView.text=arabicAddress?.get(0)?.countryName

        }
        else {
            binding.mapActCityTextView.text=address?.get(0)?.adminArea/*?.substringBefore(",")*/
            binding.mapActCountryTextView.text=address?.get(0)?.countryName

        }
        binding.mapActSelectButton.visibility = View.VISIBLE

    }else {
        binding.mapActCityTextView.text="N/A"
        binding.mapActCountryTextView.text="N/A"
        binding.mapActSelectButton.visibility = View.GONE
    }
}



                // Refresh map to display the updated marker position
                mapView.invalidate()
                return true
            }

            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                return false
            }
        })

        binding.mapActSelectButton.setOnClickListener{
                if (source == "settings")
                mapViewModel.saveLocation(lat, lon)
                else if (source == "favourite")
                mapViewModel.addFavCity(lat,lon,englishCity,englishCountry,arabicCity,arabicCountry)
                else
                {
                    val resultIntent = Intent()
                    resultIntent.putExtra("lat", lat)
                    resultIntent.putExtra("lon", lon)
                    resultIntent.putExtra("city",englishCity)
                    resultIntent.putExtra("country",englishCountry)
                    resultIntent.putExtra("arabiccity",arabicCity)
                    resultIntent.putExtra("arabiccountry",arabicCountry)
                    setResult(Activity.RESULT_OK, resultIntent)
                    Log.i("TAG", "on notification if  ")
                    finish()
                }

            this@MapActivity.finish()
        }

// Add the MapEventsOverlay to the map
        mapView.overlays.add(mapEventsOverlay)

        }
    private fun mapNetwork(){
        if (isOnline(this))
        {
            binding.mapInternetView.visibility = View.VISIBLE
            binding.mapView.visibility = View.VISIBLE
            binding.mapNoInternetview.visibility = View.GONE
        }
        else
        {
            binding.mapInternetView.visibility = View.GONE
            binding.mapView.visibility = View.GONE
            binding.mapNoInternetview.visibility = View.VISIBLE
        }
    }
    }
