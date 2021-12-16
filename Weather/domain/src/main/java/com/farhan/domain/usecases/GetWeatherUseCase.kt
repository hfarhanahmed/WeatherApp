package com.farhan.domain.usecases

import com.farhan.domain.common.SchedulerProvider
import com.farhan.domain.extensions.compose
import com.farhan.domain.repositories.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) {
    operator fun invoke(latitude: Double, longitude: Double) = weatherRepository.getWeatherByCoords(latitude, longitude).compose(schedulerProvider)
}