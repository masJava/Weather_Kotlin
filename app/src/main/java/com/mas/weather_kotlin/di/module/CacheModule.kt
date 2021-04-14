package com.mas.weather_kotlin.di.module

import androidx.room.Room
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IWeatherCache
import com.mas.weather_kotlin.mvp.model.entity.room.cache.RoomWeatherCache
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
    fun weatherCache(db: Database): IWeatherCache = RoomWeatherCache(db)

}