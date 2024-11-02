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
import com.example.weatherforecast.ui.map.viewmodel.MapViewModel
import com.example.weatherforecast.ui.map.viewmodel.MapViewModelFactory
import com.google.android.gms.location.LocationServices
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    lateinit var mapView: MapView
    private lateinit var binding: ActivityMapBinding
 lateinit var mapViewModel: MapViewModel
 lateinit var mapViewModelFactory: MapViewModelFactory
 lateinit var geocoder: Geocoder
 lateinit var source:String
    var lat:Double = 0.0
    var lon:Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = intent.getStringExtra("source")?:"null"


        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)


        // Add a marker
        val marker: Marker = Marker(mapView)
        marker.setPosition(startPoint)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        // Set up MapEventsOverlay for detecting touch events
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                marker.position = geoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                // Display the latitude and longitude
                lat = geoPoint.latitude
                lon = geoPoint.longitude
                Log.i("TAG", "singleTapConfirmedHelper: ")

                    geocoder = Geocoder(this@MapActivity)


                   var address =  geocoder.getFromLocation(lat,lon,1)
                   binding.mapActCard.visibility = View.VISIBLE
                if (!address.isNullOrEmpty())
                {
                    binding.mapActCityTextView.text=address?.get(0)?.adminArea/*?.substringBefore(",")*/
                    binding.mapActCountryTextView.text=address?.get(0)?.countryName
                    binding.mapActSelectButton.visibility = View.VISIBLE

                }else {
                    binding.mapActCityTextView.text="N/A"
                    binding.mapActCountryTextView.text="N/A"
                    binding.mapActSelectButton.visibility = View.GONE
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
                mapViewModel.addFavCity(lat,lon,binding.mapActCityTextView.text.toString(),binding.mapActCountryTextView.text.toString())
                else
                {
                    val resultIntent = Intent()
                    resultIntent.putExtra("lat", lat)
                    resultIntent.putExtra("lon", lon)
                    resultIntent.putExtra("city",binding.mapActCityTextView.text.toString())
                    resultIntent.putExtra("country",binding.mapActCountryTextView.text.toString())
                    setResult(Activity.RESULT_OK, resultIntent)
                    Log.i("TAG", "on notification if  ")
                    finish()
                }

            this@MapActivity.finish()
        }

// Add the MapEventsOverlay to the map
        mapView.overlays.add(mapEventsOverlay)

        }
    }
