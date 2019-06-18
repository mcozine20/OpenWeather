package com.example.openweather.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.openweather.R
import com.example.openweather.data.CityResult
import com.example.openweather.network.WeatherAPI
import kotlinx.android.synthetic.main.activity_weather_detail.*
import kotlinx.android.synthetic.main.activity_weather_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class WeatherDetailActivity : AppCompatActivity() {

    private val HOST_URL = "https://api.openweathermap.org"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        var intentThatStartedThis = getIntent()
        var cityName = intentThatStartedThis.getStringExtra(Intent.EXTRA_TEXT)

        val retrofit = Retrofit.Builder()
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var weatherAPI = retrofit.create(WeatherAPI::class.java)

        val weatherCall = weatherAPI.getWeather(cityName,
            "metric",
            "e63ae83c97a1097139b8e39356973399"
            )

        weatherCall.enqueue(object: Callback<CityResult> {
            override fun onFailure(call: Call<CityResult>, t: Throwable) {
                //tvCityName.text = t.message
                finish()
            }
            override fun onResponse(call: Call<CityResult>, response: Response<CityResult>) {
                if (response.isSuccessful) {
                    val cityResult = response.body()
                    val calendar = Calendar.getInstance()
                    val sunrise = cityResult?.sys?.sunrise!!.toLong()
                    calendar.setTimeInMillis(sunrise * 1000)
                    tvSunrise.text = SimpleDateFormat("HH:mm").format(calendar.time)
                    val sunset = cityResult?.sys?.sunset!!.toLong()
                    calendar.setTimeInMillis(sunset * 1000)
                    tvSunset.text = SimpleDateFormat("HH:mm").format(calendar.time)

                    tvCityName.text = cityName
                    tvTemp.text = cityResult?.main?.temp.toString()
                    tvDescription.text = cityResult?.weather?.get(0)?.description.toString()
                    tvMinTemp.text = cityResult?.main?.temp_min.toString()
                    tvMaxTemp.text = cityResult?.main?.temp_max.toString()
                    tvHumidity.text = cityResult?.main?.humidity.toString()
                    tvWindSpeed.text = cityResult?.wind?.speed.toString()
                    Glide.with(this@WeatherDetailActivity)
                        .load(("https://openweathermap.org/img/w/" + cityResult?.weather?.get(0)?.icon + ".png"))
                        .into(ivWeather)
                }
                else {
                    finish()
                }
            }
        })

    }
}