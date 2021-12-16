package com.farhan.domain.repositories

import com.farhan.domain.common.WResult
import com.farhan.domain.entities.WeatherEntity
import io.reactivex.Completable
import io.reactivex.Single

interface WeatherRepository {
    fun getWeatherByCoords(latitude: Double, longitude: Double): Single<WResult<WeatherEntity>>
    fun getWeatherByCity(city: String): Single<WResult<WeatherEntity>>
    fun addFavoriteCity(city: String)
}