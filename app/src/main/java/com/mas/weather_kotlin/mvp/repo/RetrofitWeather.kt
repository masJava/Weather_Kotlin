package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.BuildConfig
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
                    BuildConfig.API_KEY,
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

    override fun getJsonStr(lat: String, lon: String) =
        networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                api.getWeatherString(
                    lat,
                    lon,
                    BuildConfig.API_KEY,
                    "metric"
                ).flatMap {
                    Single.fromCallable { it }
                }
            } else {
                Single.fromCallable { null }
            }
        }.subscribeOn(Schedulers.io())

    override fun getCities(city: String) = networkStatus.isOnlineSingle().flatMap { isOnline ->
        if (isOnline) {
            api.getCities(city, "1", BuildConfig.API_KEY)
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

    override fun getCitiesGPS(lat: Double, lon: Double) =
        networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                api.getCitiesGPS(
                    lat.toString(),
                    lon.toString(),
                    "1",
                    BuildConfig.API_KEY
                )
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