package com.example.weatherforecast.ui.notifications.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.LocationDataSource
import com.example.weatherforecast.database.WeatherDataBase
import com.example.weatherforecast.databinding.FragmentFavouriteBinding
import com.example.weatherforecast.databinding.FragmentNotificationsBinding
import com.example.weatherforecast.model.CityDataState
import com.example.weatherforecast.model.NotificationDataState
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.WeatherAlarmReceiver
import com.example.weatherforecast.ui.favourite.view.FavouriteFragmentListAdapter
import com.example.weatherforecast.ui.favourite.viewmodel.FavouriteFragmentViewModelFactory
import com.example.weatherforecast.ui.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforecast.ui.home.view.HomeFragment
import com.example.weatherforecast.ui.notificationdialogfragment.view.SetNotificationDialogFragment
import com.example.weatherforecast.ui.notifications.viewmodel.NotificationsViewModel
import com.example.weatherforecast.ui.notifications.viewmodel.NotificationsViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationsFragmentViewModelFactory: NotificationsViewModelFactory
    private lateinit var notificationsViewModel: NotificationsViewModel


    lateinit var notificationsFragmentListAdapter: NotificationsFragmentListAdapter
    lateinit var notificationsLayoutManager: LinearLayoutManager

lateinit var builder:AlertDialog.Builder


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
    binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsFragmentViewModelFactory = NotificationsViewModelFactory((Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(
            RetrofitHelper
        )
            , LocalDataSource.getInstance(
                WeatherDataBase.getInstance(this.requireContext()).getWeatherDAO()
                , PreferenceManager.getDefaultSharedPreferences(this.context as Context)
            ), LocationDataSource.getInstance(LocationServices.getFusedLocationProviderClient(requireActivity()))
        )))

        notificationsViewModel = ViewModelProvider(this,notificationsFragmentViewModelFactory).get(NotificationsViewModel::class)

        notificationsFragmentListAdapter = NotificationsFragmentListAdapter({ it ->

              val builder = AlertDialog.Builder(context)
                builder.setMessage("If you proceed, you will remove ${it.city}")
                builder.setPositiveButton("Proceed") { dialog, _ ->
                    cancelAlarm(this.requireContext(),(it.lat+it.lon).toInt())
                    notificationsViewModel.removeNotification(it)
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            },{})

        notificationsLayoutManager = LinearLayoutManager(this.context)
        notificationsLayoutManager.orientation = RecyclerView.VERTICAL
        binding.notificationsRecyclerView.apply {
            adapter = notificationsFragmentListAdapter
            layoutManager = notificationsLayoutManager
        }
notificationsViewModel.getNotifications()
        lifecycleScope.launch { notificationsViewModel.postStateFlow.collectLatest {
            when(it){
                is NotificationDataState.Success -> {
                    notificationsFragmentListAdapter.submitList(it.data)
                }
                is NotificationDataState.Failure -> {}
                else ->{} //do something

            }
        } }


        binding.floatingActionButton3.setOnClickListener {
            if (checkOverApplicationPermissions()) {
                val dialog = SetNotificationDialogFragment()
                dialog.show(childFragmentManager, "MyDialogFragment")
            } else {
                enableOverOtherAppService()
                if (checkOverApplicationPermissions()) {
                    val dialog = SetNotificationDialogFragment()
                    dialog.show(childFragmentManager, "MyDialogFragment")
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()


        }



    fun checkOverApplicationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) and above
            return requireActivity().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        // For devices running lower than Android 13, permission is not required
        return true
    }

    fun enableOverOtherAppService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Only request on Android 13 and above
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 2)
        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WeatherAlarmReceiver::class.java) // Use the same receiver as when setting the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // Use the same request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)

        // Optionally, cancel the pending intent itself if you no longer need it
        pendingIntent.cancel()
    }
}

