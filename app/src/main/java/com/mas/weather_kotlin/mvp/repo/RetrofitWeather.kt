package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IWeatherCache
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RetrofitWeather(
    val api: IDataSource,
    val networkStatus: INetworkStatus,
    val weatherCache: IWeatherCache
) : IOpenWeather {

    override fun getWeather(lat: String, lon: String) =
        networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                api.getWeather(
                    lat,
                    lon,
                    "939ed75243a7e3da09aee0483d738411",
                    "metric"
                ).flatMap { weather ->
                    Single.fromCallable {
                        weatherCache.insert(weather)
                        weather
                    }

                }
            } else {
                Single.fromCallable {
                    weatherCache.getWeather(lat + lon)
                }
            }

        }.subscribeOn(Schedulers.io())

    override fun getCities(city: String) = networkStatus.isOnlineSingle().flatMap { isOnline ->
        if (isOnline) {
            api.getCities(city, "5", "939ed75243a7e3da09aee0483d738411")
                .flatMap { cities ->
                    Single.fromCallable {
                        cities
                    }
                }
        } else {
            Single.fromCallable {
                mutableListOf<CitiesRequestModel>()
            }
        }

    }.subscribeOn(Schedulers.io())

}