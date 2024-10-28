package com.example.weatherforecast.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DayweatherlayoutBinding
import com.example.weatherforecast.databinding.HourweatherlayoutBinding
import com.example.weatherforecast.model.WeatherData
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyDiffUtil : DiffUtil.ItemCallback<WeatherData>(){
    override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem.equals(newItem)
    }

}


class HomeFragmentHourlyListAdapter(var temp:String): ListAdapter<WeatherData, HomeFragmentHourlyListAdapter.ViewHolder>(MyDiffUtil()) {
lateinit var binding: HourweatherlayoutBinding
 lateinit var    numberFormat:NumberFormat

    // Format the number according to the locale
 //numberFormat.format(number)
 val URL = "https://openweathermap.org/img/wn/"
    class ViewHolder( var binding: HourweatherlayoutBinding): RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= HourweatherlayoutBinding.inflate(inflater,parent,false)
        numberFormat = NumberFormat.getInstance(Locale.getDefault())
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.homeFragmentHourlyTempDegree.text=numberFormat.format(current.main?.temp?.toInt())
        val hour24 = current.dt_txt.substring(11, 13).toInt()
        val amPm = if (hour24 < 12) holder.itemView.context.getString(R.string.am) else holder.itemView.context.getString(R.string.pm)
        val hour12 = if (hour24 % 12 == 0) 12 else hour24?.rem(12)
        binding.homeFragmentHourlyHourTextView.text= "${numberFormat.format(hour12)} $amPm"
        holder.binding.homeFragHourlyUnitTextView.text = if(temp == "1"){"F"} else {if (temp == "2"){"K"} else "C"}
        Glide.with(holder.itemView.context).load(URL + current.weather?.get(0)?.icon+"@2x.png").apply(
            RequestOptions().override(150, 150)).into( binding.HomeFragmentHourlyWeatherIcon)


    }
}




class HomeFragmentDailyListAdapter: ListAdapter<WeatherData, HomeFragmentDailyListAdapter.ViewHolder>(MyDiffUtil()) {
    lateinit var binding: DayweatherlayoutBinding
    lateinit var    numberFormat:NumberFormat

    val URL = "https://openweathermap.org/img/wn/"
    class ViewHolder( var binding: DayweatherlayoutBinding): RecyclerView.ViewHolder(binding.root){
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= DayweatherlayoutBinding.inflate(inflater,parent,false)
        numberFormat = NumberFormat.getInstance(Locale.getDefault())
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.homeFragmentDailyTempSmallDegree.text = numberFormat.format(current.main.temp_min.toInt())
        holder.binding.homeFragmentDailyGreatTempDegree.text = numberFormat.format(current.main.temp_max.toInt())
        holder.binding.HomeFragmentDailyDetailsTextView.text = current.weather.get(0).description
        val date: Date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
            current.dt_txt.substring(0,11)
        )
        holder.binding.homeFragmentDailyDayTextView.text= SimpleDateFormat("EEE dd/MM", Locale.getDefault()).format(date)

        Glide.with(holder.itemView.context).load(URL + current.weather?.get(0)?.icon+"@2x.png").apply(
            RequestOptions().override(150, 150)).into( binding.homeFragmentDailyIcon)


    }
}