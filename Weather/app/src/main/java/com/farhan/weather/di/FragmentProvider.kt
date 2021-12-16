package com.farhan.weather.di

import com.farhan.weather.weather.WeatherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideWeatherFragment(): WeatherFragment
}