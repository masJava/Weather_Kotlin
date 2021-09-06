package com.mas.weather_kotlin.di.module

import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.repo.RetrofitWeather
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WeatherModule {

    @Singleton
    @Provides
    fun weather(
        api: IDataSource,
        networkStatus: INetworkStatus
    ): IOpenWeather = RetrofitWeather(api, networkStatus)
}