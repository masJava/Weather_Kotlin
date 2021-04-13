package com.mas.weather_kotlin.di.module

import androidx.room.Room
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IGithubRepositoryCache
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IGithubUsersCache
import com.mas.weather_kotlin.mvp.model.entity.room.cache.RoomGithubUsersCache
import com.mas.weather_kotlin.mvp.model.entity.room.db.Database
import com.mas.weather_kotlin.ui.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CacheModule {

    @Singleton
    @Provides
    fun dataBase(app: App) =
        Room.databaseBuilder(app, Database::class.java, Database.DB_NAME).build()

    @Singleton
    @Provides
    fun usersCache(db: Database): IGithubUsersCache = RoomGithubUsersCache(db)

//    @Singleton
//    @Provides
//    fun repositoryCache(db: Database): IGithubRepositoryCache = RoomGithubRepositoryCache(db)
}