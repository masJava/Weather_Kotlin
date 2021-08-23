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
    @Expose val temp: Temp? = null,
    @Expose val feelsLike: Feels_like? = null,
    @Expose val pressure: Int = 0,
    @Expose val humidity: Int = 0,
    @Expose val dew_point: Float = 0f,
    @Expose val uvi: Float = 0f,
    @Expose val clouds: Int = 0,
    @Expose val wind_speed: Float = 0f,
    @Expose val rain: Float = 0f,
    @Expose val snow: Float = 0f,
    @Expose val wind_deg: Int = 0,
    @Expose val weather: List<WeatherRestModel>? = null,
    @Expose val pop: Float = 0f
) : Parcelable