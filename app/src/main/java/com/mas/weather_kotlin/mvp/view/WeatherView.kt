package com.mas.weather_kotlin.mvp.view

import com.github.mikephil.charting.data.CombinedData
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
    fun setCurrentWeatherIco(weatherIcoId: Int)
    fun setCurrentBackground(background: Int?)
    fun setCurrentToolbarColor(toolbarColor: Int?)
    fun hintVisible(visible: Boolean)
    fun hideSwipeRefresh()
    fun swipeRefreshVisible()

    //    fun setChart(dailyTemp: List<Entry>, dailyWind: List<Entry>, xAxis: List<String>, dailyRain: List<BarEntry>)
    fun setChart(
        data: CombinedData,
        xAxis: List<String>,
        lAxisMinMax: Pair<Float, Float>,
        rAxisMinMax: Pair<Float, Float>
    )

}