package com.example.weatherforecast.ui.settings.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.statusbar)) // Replace 'yourColor' with your color resource or color value
        findPreference<ListPreference>("list_preference_1")
            ?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue){
                    "0" -> {
                        ContextUtils.updateLocale(this@SettingsFragment.context as Context,
                            Locale("ar")
                        ); this@SettingsFragment.requireActivity().recreate()}
                    "1"-> {ContextUtils.updateLocale(this@SettingsFragment.context as Context,Locale("ar")); this@SettingsFragment.requireActivity().recreate()}
                else -> { ContextUtils.updateLocale(this@SettingsFragment.context as Context,
                    Locale.getDefault()); this@SettingsFragment.requireActivity().recreate()}
                }
                true
            }
        findPreference<ListPreference>("list_preference_4")?.setOnPreferenceChangeListener { preference, newValue ->
            if(newValue == "1")
            {
                intent = Intent(this@SettingsFragment.requireActivity(),MapActivity::class.java)

                startActivity(intent)
            }
        true
        }
    }


}
