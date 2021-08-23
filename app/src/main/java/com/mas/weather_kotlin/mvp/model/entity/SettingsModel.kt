package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsModel(
    var city: String = "",
    var lat: String = "",
    var lon: String = "",
    var position: Int = 0,
    var gpsKey: Boolean = false,
    var percentRain: Boolean = false,
    var jsonTxt: String = "",
    var swRain: Boolean = false,
    var swTemp: Boolean = false,
    var swWind: Boolean = false,
) : Parcelable {

}