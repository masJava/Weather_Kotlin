package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel

interface IGithubUsersCache {
    fun insert(weather: WeatherRequestRestModel)
    fun getWeather(id:String): WeatherRequestRestModel
}