package com.example.weatherforecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val Base_URL = "https://api.openweathermap.org/data/2.5/"
    private  val retrofitInstance = Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(
        GsonConverterFactory.create()).build()
    val service = retrofitInstance.create(ApiInterface::class.java)
}