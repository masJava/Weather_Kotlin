package com.mas.weather_kotlin.mvp.model.entity.room.dao

import androidx.room.*
import com.mas.weather_kotlin.mvp.model.entity.room.RoomWeather

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(roomWeather: RoomWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg roomWeather: RoomWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(roomWeather: List<RoomWeather>)

    @Update
    fun update(roomWeather: RoomWeather)

    @Update
    fun update(vararg roomWeather: RoomWeather)

    @Update
    fun update(roomWeather: List<RoomWeather>)

    @Delete
    fun delete(roomWeather: RoomWeather)

    @Delete
    fun delete(vararg roomWeather: RoomWeather)

    @Delete
    fun delete(roomWeather: List<RoomWeather>)

    @Query("SELECT * FROM RoomWeather")
    fun getAll(): List<RoomWeather>

    @Query("SELECT * FROM RoomWeather WHERE id = :id LIMIT 1")
    fun findById(id: String): RoomWeather?


}