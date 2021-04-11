package com.mas.weather_kotlin.mvp.model.entity.any

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(@SerializedName("1h") val rain: Float = 0f) : Parcelable
