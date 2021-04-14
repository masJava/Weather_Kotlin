package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel

interface IWeatherCache {
    fun insert(weather: WeatherRequestRestModel)
    fun getWeather(id: String): WeatherRequestRestModel
}