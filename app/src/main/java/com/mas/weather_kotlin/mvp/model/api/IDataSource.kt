package com.mas.weather_kotlin.mvp.model.api

import com.google.gson.JsonObject
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface IDataSource {

    @GET("data/2.5/onecall")
    fun getWeather(
        @Query("lat") cityLat: String,
        @Query("lon") cityLon: String,
        @Query("appid") keyApi: String,
        @Query("units") units: String
    ): Single<WeatherRequestRestModel>

    @GET("data/2.5/onecall")
    fun getWeatherString(
        @Query("lat") cityLat: String,
        @Query("lon") cityLon: String,
        @Query("appid") keyApi: String,
        @Query("units") units: String
    ): Single<JsonObject>

    @GET("geo/1.0/direct")
    fun getCities(
        @Query("q") name: String,
        @Query("limit") limit: String,
        @Query("appid") keyApi: String
    ): Single<List<CitiesRequestModel>>

    @GET("geo/1.0/reverse")
    fun getCitiesGPS(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("limit") limit: String,
        @Query("appid") keyApi: String
    ): Single<List<CitiesRequestModel>>

}