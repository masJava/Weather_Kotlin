package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import io.reactivex.rxjava3.core.Single

interface IOpenWeather {
    fun getWeather(): Single<WeatherRequestRestModel>
}