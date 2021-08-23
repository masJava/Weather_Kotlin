package com.mas.weather_kotlin.mvp.repo

import com.google.gson.JsonObject
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody

interface IOpenWeather {
    fun getWeather(lat: String, lon: String): Single<WeatherRequestRestModel>
    fun getJsonStr(lat: String, lon: String): Single<JsonObject?>
    fun getCities(city: String): Single<List<CitiesRequestModel>>
    fun getCitiesGPS(lat: Double, lon: Double): Single<List<CitiesRequestModel>>
}