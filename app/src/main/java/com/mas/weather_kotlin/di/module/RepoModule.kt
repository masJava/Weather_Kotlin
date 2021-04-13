package com.mas.weather_kotlin.di.module

import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IGithubUsersCache
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.repo.RetrofitWeather
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {

    @Singleton
    @Provides
    fun users(
        api: IDataSource,
        networkStatus: INetworkStatus,
        usersCache: IGithubUsersCache
    ): IOpenWeather = RetrofitWeather(api, networkStatus, usersCache)
}