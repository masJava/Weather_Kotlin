package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetData(
    val current: WidgetDataModel = WidgetDataModel("", "", 0),
    val hourly: List<WidgetDataModel> = listOf(
        WidgetDataModel("", "", 0),
        WidgetDataModel("", "", 0),
        WidgetDataModel("", "", 0)
    ),
    val daily: List<WidgetDataModel> = listOf(
        WidgetDataModel("", "", 0),
        WidgetDataModel("", "", 0),
        WidgetDataModel("", "", 0)
    )
) : Parcelable {
}

@Parcelize
data class WidgetDataModel(
    var dt: String = "",
    var temp: String = "",
    var weatherIcoId: Int = 0
) : Parcelable {
}