package com.mas.weather_kotlin.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface WeatherView : MvpView {
    fun initRV()
    fun updateDailyList()
    fun updateHourlyList()
    fun setUpdate(update: String)
    fun setCurrentTemp(temp: String)
    fun setCurrentHum(hum: String)
    fun setCurrentWind(wind: String)
    fun setCurrentPress(press: String)
    fun setCurrentSunrise(sunrise: String)
    fun setCurrentSunset(sunset: String)
    fun setCurrentCityName(cityName: String)
    fun setCurrentWeatherIco(weatherIcoId: String)
    fun hintVisible(visible: Boolean)


}