package com.mas.weather_kotlin.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface DayInfoView : MvpView {
    fun setDay(text: String)
    fun setTitle(text: String)
    fun setTempMax(text: String)
    fun setTempMin(text: String)
    fun setDailyPressure(text: String)
    fun setDailyHumidity(text: String)
    fun setDailyRain(text: String)
    fun loadWeatherIco(url: String)
}