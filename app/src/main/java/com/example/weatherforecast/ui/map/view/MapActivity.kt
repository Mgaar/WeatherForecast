package com.example.weatherforecast.ui.map.view


import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherforecast.R
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.databinding.ActivityMapBinding
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.map.viewmodel.MapViewModel
import com.example.weatherforecast.ui.map.viewmodel.MapViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapViewModelFactory = MapViewModelFactory(Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(
            RetrofitHelper), LocalDataSource.getInstance(
                WeatherDataBase.getInstance(this).getWeatherDAO()
                , PreferenceManager.getDefaultSharedPreferences(this ))
        ))
        mapViewModel = ViewModelProvider(this,mapViewModelFactory).get(MapViewModel::class.java)


        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        val startPoint: GeoPoint = GeoPoint(30.4198, 31.5619)
        mapView.controller.setZoom(17.0)
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
                val lat = geoPoint.latitude
                val lon = geoPoint.longitude
                println("Selected Location: Lat: $lat, Lon: $lon")

                mapViewModel.saveLocation(lat, lon)
                // Refresh map to display the updated marker position
                mapView.invalidate()
                this@MapActivity.finish()
                return true
            }

            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                return false
            }
        })

// Add the MapEventsOverlay to the map
        mapView.overlays.add(mapEventsOverlay)

        }
    }
