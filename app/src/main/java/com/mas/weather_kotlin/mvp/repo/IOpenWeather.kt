package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import io.reactivex.rxjava3.core.Single

interface IOpenWeather {
    fun getWeather(lat: String, lon: String): Single<WeatherRequestRestModel>
    fun getCities(city: String): Single<List<CitiesRequestModel>>
}