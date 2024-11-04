package com.example.weatherforecast.ui

import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.LocaleList
import java.util.Locale

class ContextUtils(base: Context) : ContextWrapper(base) {
    companion object {
        fun updateLocale(context: Context, localeToSwitchTo: Locale): ContextUtils {
            val resources = context.resources
            val configuration = resources.configuration // 1

            val localeList = LocaleList(localeToSwitchTo) // 2
            LocaleList.setDefault(localeList) // 3
            configuration.setLocales(localeList) // 4

            val updatedContext = context.createConfigurationContext(configuration) // 5

            return ContextUtils(updatedContext)
        }
    }
}

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}

fun translateWeatherDescription(description: String, locale: Locale): String {
    return when (locale.language) {
        "en" -> description
        else -> when (description) {
            "clear sky" -> "سماء صافية"
            "few clouds" -> "بعض السحب"
            "scattered clouds" -> "سحب متفرقة"
            "broken clouds" -> "سحب متكسرة"
            "shower rain" -> "أمطار متقطعة"
            "rain" -> "أمطار"
            "thunderstorm" -> "عاصفة رعدية"
            "snow" -> "ثلج"
            "mist" -> "ضباب"
            else -> description // Return the original description if not matched
        }
    }
}
