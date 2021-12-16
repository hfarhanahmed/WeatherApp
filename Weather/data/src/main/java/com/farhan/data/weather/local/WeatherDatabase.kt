package com.farhan.data.weather.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(WeatherRoomEntity::class),(FavoriteRoomEntity::class)], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
}