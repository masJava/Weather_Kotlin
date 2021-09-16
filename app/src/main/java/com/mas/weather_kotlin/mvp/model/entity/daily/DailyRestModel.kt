package com.mas.weather_kotlin.mvp.model.entity.daily

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.mas.weather_kotlin.mvp.model.entity.any.WeatherRestModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyRestModel(
    @Expose val dt: Long = 0L,
    @Expose val sunrise: Long = 0L,
    @Expose val sunset: Long = 0L,
    @Expose val moonrise: Long = 0L,
    @Expose val moonset: Long = 0L,
    @Expose val moon_phase: Float = 0f,
    @Expose val temp: Temp = Temp(),
    @Expose val feels_like: Feels_like = Feels_like(),
    @Expose val pressure: Int = 0,
    @Expose val humidity: Int = 0,
    @Expose val dew_point: Float = 0f,
    @Expose val uvi: Float = 0f,
    @Expose val clouds: Int = 0,
    @Expose val wind_speed: Float = 0f,
    @Expose val rain: Float = 0f,
    @Expose val snow: Float = 0f,
    @Expose val wind_deg: Int = 0,
    @Expose val weather: List<WeatherRestModel> = emptyList(),
    @Expose val pop: Float = 0f
) : Parcelable