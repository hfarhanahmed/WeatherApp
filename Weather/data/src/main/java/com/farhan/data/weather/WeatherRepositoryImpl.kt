package com.farhan.data.weather

import com.farhan.data.common.ErrorMapper
import com.farhan.data.weather.local.FavoriteDao
import com.farhan.data.weather.local.FavoriteRoomEntity
import com.farhan.data.weather.local.WeatherDao
import com.farhan.data.weather.remote.WeatherService
import com.farhan.domain.common.WResult
import com.farhan.domain.entities.WeatherEntity
import com.farhan.domain.extensions.doOnWeatherSuccess
import com.farhan.domain.extensions.isOutOfDate
import com.farhan.domain.extensions.toResult
import com.farhan.domain.repositories.WeatherRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val favoriteDao: FavoriteDao,
    private val weatherService: WeatherService,
    private val weatherEntityMapper: WeatherEntityMapper,
    private val errorMapper: ErrorMapper
): WeatherRepository {

    override fun getWeatherByCoords(latitude: Double, longitude: Double): Single<WResult<WeatherEntity>> {
        val latitude = "%.2f".format(latitude).toDouble()
        val longitude = "%.2f".format(longitude).toDouble()

        val cache = weatherDao.getWeatherByCoords(latitude, longitude)
                .map { weatherEntityMapper.mapFrom(it).toResult() }
                .onErrorReturn { errorMapper.mapFrom(it).toResult() }

        val network = weatherService.getWeatherByCoords(latitude, longitude)
                .map { weatherEntityMapper.mapFrom(it).toResult() }
                .onErrorReturn { errorMapper.mapFrom(it).toResult() }
                .doOnWeatherSuccess { weatherDao.insertWeather(weatherEntityMapper.mapFrom(it)).subscribe() }

        return Single.concat(cache, network)
            .filter { it is WResult.Success && it.data.lastUpdatedAt.isOutOfDate().not() }
            .firstOrError()
            .onErrorResumeNext(network)
    }

    override fun getWeatherByCity(city: String): Single<WResult<WeatherEntity>> {
        return weatherService.getWeatherByCity(city)
            .map { weatherEntityMapper.mapFrom(it).toResult() }
            .onErrorReturn { errorMapper.mapFrom(it).toResult() }
    }

    override fun addFavoriteCity(city: String) {
        favoriteDao.insert(FavoriteRoomEntity(0, city))
    }
}