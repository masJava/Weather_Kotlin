package com.mas.weather_kotlin.mvp.model.entity.current

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.mas.weather_kotlin.mvp.model.entity.any.Rain
import com.mas.weather_kotlin.mvp.model.entity.any.Snow
import com.mas.weather_kotlin.mvp.model.entity.any.WeatherRestModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentRestModel(
    @Expose val dt: Long = 0L,
    @Expose val sunrise: Long = 0L,
    @Expose val sunset: Long = 0L,
    @Expose val temp: Float = 0f,
    @Expose val feelsLike: Float = 0f,
    @Expose val pressure: Int = 0,
    @Expose val humidity: Int = 0,
    @Expose val dew_point: Float = 0f,
    @Expose val uvi: Float = 0f,
    @Expose val clouds: Int = 0,
    @Expose val wind_speed: Float = 0f,
    @Expose val wind_deg: Int = 0,
    @Expose val weather: List<WeatherRestModel> = emptyList(),
    @Expose val rain: Rain = Rain(),
    @Expose val snow: Snow = Snow()
) : Parcelable