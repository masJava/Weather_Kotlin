package com.mas.weather_kotlin.mvp.view.list

interface IDailyItemView : IItemView {
    fun setDay(text: String)
    fun setTempMax(text: String)
    fun setTempMin(text: String)
    fun setDailyPressure(text: String)
    fun setDailyHumidity(text: String)
    fun setDailyRain(text: String)
    fun loadWeatherIco(url: String)
}