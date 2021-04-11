package com.mas.weather_kotlin.mvp.model.entity.any

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Snow(@SerializedName("1h") val snow: Float = 0f) : Parcelable
