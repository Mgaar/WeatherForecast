package com.example.weatherforecast

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.ui.ContextUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var localeToSwitch : Locale

    private lateinit var binding: ActivityMainBinding
    val REQUEST_LOCATION_CODE = 5
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


       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        lifecycleScope.launch(Dispatchers.IO) {
            while (true)
            {
                if(isOnline())
                {
                    withContext(Dispatchers.Main)  {
                        binding.textView4.visibility = View.GONE
                    }
                }
                else {
                    withContext(Dispatchers.Main)  {
                        binding.textView4.visibility = View.VISIBLE

                    }
                }
            }


        }

    }

    override fun onStart() {
        super.onStart()
        if(checkPermissions()){
            if(isLocationEnabled())
            {

            }else{
                enableLocationService()
            }
        }else
        {
            ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_LOCATION_CODE)
        }

        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val lang =  sharedPreferences.getString("list_preference_1", "1")
        Log.i("TAG", "attachBaseContext: "+lang)
        if (lang == "2") {
            Log.i("TAG", "attachBaseContext: "+Locale.getDefault().language)
            localeToSwitch = Locale.getDefault()

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
        Locale.setDefault(localeToSwitch)

    }

    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

companion object{
   val localeDefault = Locale.getDefault()

}
    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
       val lang =  sharedPreferences.getString("list_preference_1", "1")
        Log.i("TAG", "attachBaseContext: "+lang)
        if (lang == "2") {
            Log.i("TAG", "attachBaseContext: "+Locale.getDefault().language)
            localeToSwitch = localeDefault

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

    fun checkPermissions():Boolean{
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    fun enableLocationService(){
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER))
    }




}

