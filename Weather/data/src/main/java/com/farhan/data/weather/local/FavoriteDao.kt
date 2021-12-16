package com.farhan.data.weather.local

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: FavoriteRoomEntity): Completable

    @Query("SELECT * from Favorite where city = :city ")
    fun getFavorite(city: String): Single<WeatherRoomEntity>

    @Update
    fun update(favorite: FavoriteRoomEntity): Completable

    @Delete
    fun delete(favorite: FavoriteRoomEntity): Completable

    @Query("SELECT * FROM Favorite")
    fun getAll(): List<FavoriteRoomEntity>

}