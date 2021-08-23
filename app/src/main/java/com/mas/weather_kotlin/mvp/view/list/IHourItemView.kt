package com.mas.weather_kotlin.mvp.view.list

interface IHourItemView : IItemView {
    fun setHour(text: String)
    fun setRainfall(text: String)
    fun setTemp(text: String)
    fun loadWeatherIco(url: Int)
}