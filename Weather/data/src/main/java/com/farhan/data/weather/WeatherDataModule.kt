package com.farhan.data.weather

import com.farhan.domain.di.PerApplication
import com.farhan.domain.repositories.WeatherRepository
import dagger.Binds
import dagger.Module

@Module
abstract class WeatherDataModule {

    @Binds
    @PerApplication
    abstract fun providesWeatherRepository(weatherRepository: WeatherRepositoryImpl): WeatherRepository
}