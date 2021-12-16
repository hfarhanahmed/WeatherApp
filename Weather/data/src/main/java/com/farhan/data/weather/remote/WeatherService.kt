package com.farhan.data.weather.remote

import com.farhan.data.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    fun getWeatherByCoords(@Query("lat") lat: Double,
                           @Query("lon") lon: Double,
                           @Query("appid") id: String = BuildConfig.ApiKey): Single<WeatherResponse>

    @GET("weather")
    fun getWeatherByCity(@Query("q") city: String,
                           @Query("appid") id: String = BuildConfig.ApiKey): Single<WeatherResponse>
}