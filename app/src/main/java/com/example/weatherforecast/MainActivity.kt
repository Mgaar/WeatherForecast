package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.ContextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favourite, R.id.navigation_notifications,R.id.navigation_settings
            )
        )
        val preferences = PreferenceManager.getDefaultSharedPreferences(this).all

        preferences.forEach {

            Log.i("Preferences", "${it.key} -> ${it.value}")
        }

       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



//        val repo = Repository.getInstance(RemoteDataSource.getInstance(RetrofitHelper))
//        GlobalScope.launch(Dispatchers.IO) {
//            val flow = repo.getWeatherUpdate(44.34,10.99)
//            flow.collect{response -> Log.i("TAG", "onCreate: $response.cod ${response.city} ${response.list.toString()}")}
//        }
    }

lateinit var localeToSwitch : Locale
    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
       val lang =  sharedPreferences.getString("list_preference_1", "1")
        Log.i("TAG", "attachBaseContext: "+lang)
        if (lang == "2")
        {
            localeToSwitch = Locale.getDefault()
//           super.attachBaseContext(newBase)
        }
        else
        {
            if (lang == "0")
            {
                localeToSwitch = Locale("ar")
            }
            else if (lang == "1")
                localeToSwitch = Locale("en")
        }
        val localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitch)
        super.attachBaseContext(localeUpdatedContext)

    }
}

