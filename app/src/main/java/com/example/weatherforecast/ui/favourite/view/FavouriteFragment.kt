package com.example.weatherforecast.ui.favourite.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.example.weatherforecast.model.CityDataState
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.RemoteDataSource
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.ui.favourite.viewmodel.FavouriteFragmentViewModelFactory
import com.example.weatherforecast.ui.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforecast.ui.home.view.HomeFragment
import com.example.weatherforecast.ui.isOnline
import com.example.weatherforecast.ui.map.view.MapActivity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private lateinit var favFragmentViewModelFactory: FavouriteFragmentViewModelFactory
    private lateinit var favViewModel: FavouriteViewModel

    private lateinit var binding: FragmentFavouriteBinding

    lateinit var favCityListAdapter: FavouriteFragmentListAdapter
    lateinit var favCityLayoutManager: LinearLayoutManager

    lateinit var  builder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
favFragmentViewModelFactory = FavouriteFragmentViewModelFactory((Repository.getInstance(remoteDataSource = RemoteDataSource.getInstance(
    RetrofitHelper
)
    , LocalDataSource.getInstance(
        WeatherDataBase.getInstance(this.requireContext()).getWeatherDAO()
        , PreferenceManager.getDefaultSharedPreferences(this.context as Context)
    ), LocationDataSource.getInstance(LocationServices.getFusedLocationProviderClient(requireActivity()))
)))

        if (childFragmentManager.backStackEntryCount > 0) {
            binding.detailsLayout.visibility = View.VISIBLE
            binding.favListLayout.visibility=View.GONE
        }
        else{
            binding.detailsLayout.visibility = View.GONE
            binding.favListLayout.visibility=View.VISIBLE
        }

        favViewModel = ViewModelProvider(this,favFragmentViewModelFactory).get(FavouriteViewModel::class)

        favCityListAdapter = FavouriteFragmentListAdapter(
            {  val builder = AlertDialog.Builder(context)
        builder.setMessage("If you proceed, you will remove ${it.city}")
        builder.setPositiveButton("Proceed") { dialog, _ ->
            favViewModel.removeFavCity(it)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()},{
            if (isOnline(this.context as Context))
            {
                val fragment = HomeFragment() // No arguments needed
                this@FavouriteFragment.requireActivity().supportFragmentManager.commit {
                    binding.detailsLayout.visibility = View.VISIBLE
                    binding.favListLayout.visibility=View.GONE
                    val homeFragment = HomeFragment().apply {
                        arguments = Bundle().apply {
                            putDouble("lat", it.lat)
                            putDouble("lon",it.lon)
                            putString("source","fav")
                        }
                    }
                    childFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainerView2.id, homeFragment)
                        .addToBackStack("favdetail") // Optional, to allow back navigation
                        .commit()
                }
            }
            else {
                Toast.makeText(this.context,"NO INTERNET Connection ",Toast.LENGTH_SHORT).show()
            }
            }
            )
        binding.floatingActionButton2.setOnClickListener{
childFragmentManager.popBackStack()
            binding.detailsLayout.visibility = View.GONE
            binding.favListLayout.visibility=View.VISIBLE
        }
        favCityLayoutManager = LinearLayoutManager(this.context)
        favCityLayoutManager.orientation = RecyclerView.VERTICAL
        binding.FavouriteRecyclerView.apply {
            adapter = favCityListAdapter
            layoutManager = favCityLayoutManager
        }

        favViewModel.getFavCities()
        lifecycleScope.launch { favViewModel.postStateFlow.collectLatest {
            when(it){
                is CityDataState.Success -> {
favCityListAdapter.submitList(it.data)
                }
                is CityDataState.Failure -> {}
                else ->{} //do something

            }
        } }

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this@FavouriteFragment.requireActivity(), MapActivity::class.java)
            intent.putExtra("source","favourite")
            startActivity(intent)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Check if `HomeFragment` is on the back stack and pop it
                if (childFragmentManager.backStackEntryCount > 0) {
                    binding.detailsLayout.visibility = View.GONE
                    binding.favListLayout.visibility=View.VISIBLE

                } else {
                    // Disable the callback and delegate the back press to the activity
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}


