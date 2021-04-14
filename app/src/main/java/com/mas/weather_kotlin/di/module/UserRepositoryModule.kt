package com.mas.weather_kotlin.di.module

import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IGithubRepositoryCache
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import com.mas.weather_kotlin.mvp.repo.IGithubUsersRepo
import com.mas.weather_kotlin.mvp.repo.RetrofitGithubUsersRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserRepositoryModule {

    @Singleton
    @Provides
    fun repos(
        api: IDataSource,
        networkStatus: INetworkStatus,
    ): IGithubUsersRepo = RetrofitGithubUsersRepository(api, networkStatus)
}