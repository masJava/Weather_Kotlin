package com.mas.weather_kotlin.mvp.model.entity.daily

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feels_like(
    @Expose val day: Float = 0f,
    @Expose val night: Float = 0f,
    @Expose val eve: Float = 0f,
    @Expose val morn: Float = 0f,
) : Parcelable