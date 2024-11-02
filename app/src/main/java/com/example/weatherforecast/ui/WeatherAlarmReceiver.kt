package com.example.weatherforecast.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavOptions
import androidx.preference.PreferenceManager
import com.example.weatherforecast.R
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.model.CurrentWeather
import com.example.weatherforecast.model.Repository

import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper

import com.example.weatherforecast.ui.notifications.view.NotificationsFragment
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch


class WeatherAlarmReceiver : BroadcastReceiver() {
    lateinit var repo: Repository
    lateinit var notificationBuilder: NotificationCompat.Builder
    override fun onReceive(context: Context, intent: Intent) {
        // This is where you fetch the weather data and notify the user
        repo = Repository.getInstance(
            remoteDataSource = RemoteDataSource.getInstance(
                RetrofitHelper
            ), LocalDataSource.getInstance(
                WeatherDataBase.getInstance(context).getWeatherDAO(),
                PreferenceManager.getDefaultSharedPreferences(context)
            ), LocationDataSource.getInstance(
                LocationServices.getFusedLocationProviderClient(context)
            )
        )
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val city = intent.getStringExtra("city")
        val alarm = intent.getBooleanExtra("alarm", false)
        Log.i("TAG", "onAlarmReceive: ")
        GlobalScope.launch {
            val cityFlow = repo.getCurrentWeatherUpdate(lat, lon)
            repo.removeNotificationCityById(city as String)
            cityFlow.collect { data ->
                showNotification(context, data, city as String, alarm,lat,lon)
            }

        }

    }


    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        currentWeather: CurrentWeather,
        city: String,
        alarm: Boolean,
        lat:Double,
        lon:Double
    ) {
        val alarmChannelId = "weather_alarm_channel"
        val notificationChannelId = "weather_notification_channel"

        // Create Notification Channels for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Alarm Channel
            val alarmChannel = NotificationChannel(
                alarmChannelId,
                "Weather Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for weather alarm notifications"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            }

            // Notification Channel
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Weather Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for regular weather notifications"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            }

            // Register channels with Notification Manager
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Intent to open the WeatherActivity when notification is tapped
        val notificationIntent = Intent(context, NotificationsFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("city", city)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Stop Alarm" Intent
        val stopAlarmIntent = Intent(context, StopAlarmReceiver::class.java)
       stopAlarmIntent.putExtra("location",(lat+lon).toInt())
        val stopAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = if (alarm){ NotificationCompat.Builder(context,alarmChannelId)
            .setSmallIcon(R.drawable.weather)
            .setContentTitle("Weather Alarm")
            .setContentText("Current weather: ${currentWeather.weather[0].description}, Temp: ${currentWeather.main.temp}")
            .setPriority(NotificationCompat.PRIORITY_MAX) // Use max for alarms
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.disablealarm, "Stop", stopAlarmPendingIntent) // Stop button
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setFullScreenIntent(pendingIntent, true) // Full-screen intent for immediate visibility
    } else {
            // Regular Notification with short sound
            NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.weather)
                .setContentTitle("Weather Update")
                .setContentText("Current weather: ${currentWeather.weather[0].description}, Temp: ${currentWeather.main.temp}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }

    class StopAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            cancelAlarm(context, intent.getIntExtra("location",0)) // Pass the request code used when setting the alarm

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1) // Cancel the alarm notification
        }

        private fun cancelAlarm(context: Context, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WeatherAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

