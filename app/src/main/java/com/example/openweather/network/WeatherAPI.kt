package com.example.openweather.network

import com.example.openweather.data.CityResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("/data/2.5/weather")
    fun getWeather(@Query("q") city: String,
                   @Query("units") units: String,
                   @Query("appid") appid: String) : Call<CityResult>
}