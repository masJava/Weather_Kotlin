package com.mas.weather_kotlin.mvp.model.entity.hourly

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.mas.weather_kotlin.mvp.model.entity.any.Rain
import com.mas.weather_kotlin.mvp.model.entity.any.Snow
import com.mas.weather_kotlin.mvp.model.entity.any.WeatherRestModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class HourlyRestModel(
    @Expose val dt: Long = 0L,
    @Expose val temp: Float = 0f,
    @Expose val feelsLike: Float = 0f,
    @Expose val pressure: Int = 0,
    @Expose val humidity: Int = 0,
    @Expose val dew_point: Float = 0f,
    @Expose val clouds: Int = 0,
    @Expose val wind_speed: Float = 0f,
    @Expose val wind_deg: Int = 0,
    @Expose val weather: List<WeatherRestModel>? = null,
    @Expose val rain: Rain? = null,
    @Expose val snow: Snow? = null,
    @Expose val pop: Float = 0f
) : Parcelable