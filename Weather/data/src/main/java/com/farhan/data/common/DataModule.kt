package com.farhan.data.common

import android.content.Context
import androidx.room.Room
import com.farhan.data.weather.local.FavoriteDao
import com.farhan.data.weather.local.WeatherDao
import com.farhan.data.weather.local.WeatherDatabase
import com.farhan.data.weather.remote.WeatherService
import com.farhan.domain.di.ApplicationContext
import com.farhan.domain.di.PerApplication
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Module
class DataModule {

    @Provides
    @PerApplication
    fun provideWeatherDao(weatherDatabase: WeatherDatabase): WeatherDao = weatherDatabase.weatherDao()

    @Provides
    @PerApplication
    fun provideFavoriteDao(weatherDatabase: WeatherDatabase): FavoriteDao = weatherDatabase.favoriteDao()

    @PerApplication
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): WeatherDatabase = Room.databaseBuilder(context, WeatherDatabase::class.java, DataConfig.WEATHER_DB_NAME).build()

    @PerApplication
    @Provides
    fun provideWeatherService(retrofit: Retrofit): WeatherService = retrofit.create(WeatherService::class.java)

    @PerApplication
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(DataConfig.WEATHER_ENDPOINT)
        .build()

    @PerApplication
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor { message -> Timber.i(message)}.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
}