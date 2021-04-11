package com.mas.weather_kotlin.mvp.model.api

import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface IDataSource {

    @GET("users")
    fun getUsers(): Single<List<GithubUser>>

    @GET("data/2.5/onecall")
    fun getWeather(@Query("lat") cityLat: String,
                 @Query("lon") cityLon: String,
                 @Query("appid") keyApi: String,
                 @Query("units") units: String): Single<WeatherRequestRestModel>

    @GET
    fun getUserRepos(@Url url: String): Single<List<GithubUserRepository>>

}