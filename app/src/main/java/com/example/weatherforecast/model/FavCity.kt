package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favcity_table")
data class FavCity (val lat:Double,val lon:Double,@PrimaryKey val city:String,val country:String){

}

@Entity(tableName = "notification_table")
data class NotificationCity (val lat:Double,val lon:Double,@PrimaryKey val city:String,val country:String,val time:String){

}