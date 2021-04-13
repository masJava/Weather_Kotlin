package com.mas.weather_kotlin.mvp.model.entity.room.dao

import androidx.room.*
import com.mas.weather_kotlin.mvp.model.entity.room.RoomWeather

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: RoomWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: RoomWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<RoomWeather>)

    @Update
    fun update(user: RoomWeather)

    @Update
    fun update(vararg users: RoomWeather)

    @Update
    fun update(users: List<RoomWeather>)

    @Delete
    fun delete(user: RoomWeather)

    @Delete
    fun delete(vararg users: RoomWeather)

    @Delete
    fun delete(users: List<RoomWeather>)

    @Query("SELECT * FROM RoomWeather")
    fun getAll(): List<RoomWeather>

    @Query("SELECT * FROM RoomWeather WHERE id = :id LIMIT 1")
    fun findById(id: String): RoomWeather?


}