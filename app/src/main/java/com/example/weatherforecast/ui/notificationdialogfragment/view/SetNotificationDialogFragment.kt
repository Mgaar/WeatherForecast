package com.example.weatherforecast.ui.notificationdialogfragment.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.databinding.DialogfragmentBinding
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.WeatherAlarmReceiver
import com.example.weatherforecast.ui.map.view.MapActivity
import com.example.weatherforecast.ui.notificationdialogfragment.viewmodel.SetNotificationDialogFragmentViewModel
import com.example.weatherforecast.ui.notificationdialogfragment.viewmodel.SetNotificationDialogFragmentViewModelFactory

import com.google.android.gms.location.LocationServices
import java.util.Calendar


class SetNotificationDialogFragment: DialogFragment() {
    private lateinit var binding:DialogfragmentBinding
    val calendar = Calendar.getInstance()
    var selectedYear = calendar.get(Calendar.YEAR)
    var selectedMonth = calendar.get(Calendar.MONTH)
    var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
    var selectedMinute = calendar.get(Calendar.MINUTE)
     var lat:Double=0.0
    var lon:Double=0.0
     var country:String = "N/A"
    var city:String = "N/A"
    var countryArabic:String = "N/A"
    var cityArabic:String = "N/A"
    var time = false
    var alarm = true
    private lateinit var  mapActivityLauncher:ActivityResultLauncher<Intent>
    private lateinit var setNotificationDialogFragmentViewModelFactory: SetNotificationDialogFragmentViewModelFactory
    private lateinit var setNotificationDialogFragmentViewModel: SetNotificationDialogFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNotificationDialogFragmentViewModelFactory = SetNotificationDialogFragmentViewModelFactory(
            Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(RetrofitHelper)
            , LocalDataSource.getInstance(
                    WeatherDataBase.getInstance(this.requireContext()).getWeatherDAO()
                , PreferenceManager.getDefaultSharedPreferences(this.context as Context)
            ), LocationDataSource.getInstance(LocationServices.getFusedLocationProviderClient(requireActivity()))
        ))

        setNotificationDialogFragmentViewModel = ViewModelProvider(this,setNotificationDialogFragmentViewModelFactory).get(SetNotificationDialogFragmentViewModel::class.java)


        binding.timeTextView.setOnClickListener{
        val datePickerDialog = DatePickerDialog(this.requireContext(), { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth

            val timePickerDialog = TimePickerDialog(this.requireContext(), { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                binding.timeTextView.text ="$selectedHour::$selectedMinute $selectedDay/$selectedMonth/$selectedYear"
           time = true
            }, selectedHour, selectedMinute, true)
            timePickerDialog.setOnCancelListener {

            }
            timePickerDialog.show()

        }, selectedYear, selectedMonth, selectedDay)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }
        mapActivityLauncher  = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                 lat = result.data?.getDoubleExtra("lat",0.0)?:0.0
                lon = result.data?.getDoubleExtra("lon",0.0)?:0.0
                country =result.data?.getStringExtra("country")?:"N/A"
                city =result.data?.getStringExtra("city")?:"N/A"
                countryArabic =result.data?.getStringExtra("arabiccountry")?:"N/A"
                cityArabic =result.data?.getStringExtra("arabiccity")?:"N/A"
                binding.LocationTextView.text= country
                Log.i("TAG", "onViewCreated: "+country)
            }
        }
        binding.LocationTextView.setOnClickListener{
            val intent = Intent(this@SetNotificationDialogFragment.requireActivity(), MapActivity::class.java)
            intent.putExtra("source","notification")
            mapActivityLauncher.launch(intent)
        }

        binding.buttonCancel.setOnClickListener {  dismiss() }
binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
    when (checkedId) {
       binding.radioButton1.id -> {
           alarm = true
        }

      binding.radioButton2.id -> {
           alarm = false
        }
    }
}
        binding.buttonOk.setOnClickListener {
            if (country == "N/A" )
            {
                binding.errorMessageTextView.text = "Please Choose country"
            }
            if ( time == false){
                binding.errorMessageTextView.text = "${if(binding.errorMessageTextView.text.toString() == "Please Choose country") "Please Choose country &" else "Please" } Choose Valid Time "
            }
            if (time && country!="N/A")
            {
setNotificationDialogFragmentViewModel.addNotificationCity(lat,lon,city,country,cityArabic,countryArabic,binding.timeTextView.text.toString())
                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, WeatherAlarmReceiver::class.java)
                intent.putExtra("lat",lat)
                intent.putExtra("lon",lon)
                intent.putExtra("alarm",alarm)
                intent.putExtra("country",country)
                intent.putExtra("city",city)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (lat+lon).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Set the alarm to start at the specified date and time
                calendar.apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth) // Note: Month is 0-based (0 = January, 11 = December)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)

                    // If the specified time is in the past, do not set the alarm
                    if (timeInMillis < System.currentTimeMillis()) {
                        binding.errorMessageTextView.text ="can not set alarm for past time "
                    }
                    else {
                        // Set the alarm for a one-time trigger
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        dismiss()
                    }
                }
                }




                }
            }
        }










