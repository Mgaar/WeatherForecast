package com.example.weatherforecast.ui.settings.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

import com.example.weatherforecast.R
import com.example.weatherforecast.ui.ContextUtils
import com.example.weatherforecast.ui.map.view.MapActivity
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {
lateinit var intent: Intent
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.statusbar))
        findPreference<ListPreference>("list_preference_1")
            ?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue){
                    "0" -> {
                        ContextUtils.updateLocale(this@SettingsFragment.context as Context,
                            Locale("ar")
                        ); this@SettingsFragment.requireActivity().recreate()}
                    "1"-> {ContextUtils.updateLocale(this@SettingsFragment.context as Context,Locale("en"));
                        this@SettingsFragment.requireActivity().recreate()}
                "2" -> { ContextUtils.updateLocale(this@SettingsFragment.context as Context,
                    getSystemLocale(this@SettingsFragment.context as Context));
                    Log.i("TAG", "onViewCreated: "+ getSystemLocale(this@SettingsFragment.context as Context).language)
                    this@SettingsFragment.requireActivity().recreate()}
                }
                true
            }
        findPreference<ListPreference>("list_preference_4")?.setOnPreferenceChangeListener { preference, newValue ->
            if(newValue == "1")
            {
                intent = Intent(this@SettingsFragment.requireActivity(),MapActivity::class.java)
intent.putExtra("source","settings")
                startActivity(intent)
            }
        true
        }
    }
    fun getSystemLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }


}
