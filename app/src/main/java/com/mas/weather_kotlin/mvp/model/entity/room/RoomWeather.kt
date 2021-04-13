package com.mas.weather_kotlin.mvp.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RoomWeather(
 @PrimaryKey val id: String,
 val timezone_offset: Long,
 val dt: Long,
 val sunrise: Long,
 val sunset: Long,
 val temp: Float,
 val pressure: Int,
 val humidity: Int,
 val wind_speed: Float,
 val wind_deg: Int,
 val weatherIco: String,
 val rain: Float,
 val snow: Float
)
