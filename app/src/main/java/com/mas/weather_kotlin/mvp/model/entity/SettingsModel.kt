package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsModel(
    var city: String = "",
    var lat: String = "",
    var lon: String = "",
    var position: Int = 0,
    ) : Parcelable