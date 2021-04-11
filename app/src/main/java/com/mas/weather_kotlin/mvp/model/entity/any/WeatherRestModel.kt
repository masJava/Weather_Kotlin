package com.mas.weather_kotlin.mvp.model.entity.any

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherRestModel(
    @Expose var id: Int = 0,
    @Expose val main: String = "",
    @Expose val description: String = "",
    @Expose val icon: String = ""
) : Parcelable
