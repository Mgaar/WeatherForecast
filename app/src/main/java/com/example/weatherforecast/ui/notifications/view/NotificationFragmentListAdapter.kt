package com.example.weatherforecast.ui.notifications.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FavcityBinding
import com.example.weatherforecast.model.FavCity
import com.example.weatherforecast.model.NotificationCity


class MyDiffUtil : DiffUtil.ItemCallback<NotificationCity>(){
    override fun areItemsTheSame(oldItem: NotificationCity, newItem: NotificationCity): Boolean {
        return oldItem.city == newItem.city
    }

    override fun areContentsTheSame(oldItem: NotificationCity, newItem: NotificationCity): Boolean {
        return oldItem.equals(newItem)
    }

}


class NotificationsFragmentListAdapter(val onCityClick : (NotificationCity) -> Unit, val onCardClick : (NotificationCity) -> Unit): ListAdapter<NotificationCity, NotificationsFragmentListAdapter.ViewHolder>(
    MyDiffUtil()
) {
    lateinit var binding: FavcityBinding

    class ViewHolder( var binding: FavcityBinding): RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= FavcityBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)


        binding.textView.text=current.city+" "+current.country/*+":"+current.time*/

        binding.favFragDisplayButton.text = "X"

        binding.favFragDisplayButton.setOnClickListener { onCityClick(current)
        }
    }
}