package com.mas.weather_kotlin.mvp.model.entity

data class WidgetData(
    val current: WidgetDataModel = WidgetDataModel(0L, 0f, 0),
    val hourly: List<WidgetDataModel> = listOf(
        WidgetDataModel(0L, 0f, 0),
        WidgetDataModel(0L, 0f, 0),
        WidgetDataModel(0L, 0f, 0)
    ),
    val daily: List<WidgetDataModel> = listOf(
        WidgetDataModel(0L, 0f, 0),
        WidgetDataModel(0L, 0f, 0),
        WidgetDataModel(0L, 0f, 0)
    )
) {
}

data class WidgetDataModel(
    var dt: Long = 0L,
    var temp: Float = 0f,
    var weatherIcoId: Int = 0
)