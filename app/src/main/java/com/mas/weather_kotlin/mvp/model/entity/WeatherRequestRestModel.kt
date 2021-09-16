package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.mas.weather_kotlin.mvp.model.entity.current.CurrentRestModel
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.model.entity.hourly.HourlyRestModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherRequestRestModel(
    @Expose val id: String = "",
    @Expose val lat: Float = 0f,
    @Expose val lon: Float = 0f,
    @Expose val timezone: String = "",
    @Expose val timezone_offset: Long = 0L,
    @Expose val current: CurrentRestModel = CurrentRestModel(),
    @Expose val hourly: List<HourlyRestModel> = emptyList(),
    @Expose val daily: List<DailyRestModel> = emptyList()
) : Parcelable