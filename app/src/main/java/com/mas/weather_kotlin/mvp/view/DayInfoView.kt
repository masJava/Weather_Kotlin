package com.mas.weather_kotlin.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface DayInfoView : MvpView {
    fun setDay(text: String)
    fun setTitle(text: String)
    fun setTempTable(listTemp: List<String>, listFlTemp: List<String>)
    fun setDailyPressure(text: String)
    fun setDailyHumidity(text: String)
    fun setDailyRain(text: String)
    fun setDailyWind(text: String)
    fun setWeatherIco(id: Int)
    fun setBackground(background: Int?)
    fun setToolbarColor(toolbarColor: Int?)
    fun setSunMoonData(sunrise: String, sunset: String)
}