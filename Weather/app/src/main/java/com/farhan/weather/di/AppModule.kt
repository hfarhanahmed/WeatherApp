package com.farhan.weather.di

import android.content.Context
import com.farhan.domain.common.SchedulerProvider
import com.farhan.domain.di.ApplicationContext
import com.farhan.domain.di.PerApplication
import com.farhan.weather.WeatherApplication
import com.farhan.weather.common.SchedulerProviderImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    abstract fun provideContext(application: WeatherApplication): Context

    @Binds
    @PerApplication
    abstract fun schedulerProvider(schedulerProviderImpl: SchedulerProviderImpl) : SchedulerProvider
}