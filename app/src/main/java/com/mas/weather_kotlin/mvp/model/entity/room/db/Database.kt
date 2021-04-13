package com.mas.weather_kotlin.mvp.model.entity.room.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mas.weather_kotlin.mvp.model.entity.room.RoomGithubRepository
import com.mas.weather_kotlin.mvp.model.entity.room.RoomWeather
import com.mas.weather_kotlin.mvp.model.entity.room.dao.RepositoryDao
import com.mas.weather_kotlin.mvp.model.entity.room.dao.WeatherDao

@androidx.room.Database(
    entities = [
        RoomWeather::class,
        RoomGithubRepository::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract val weatherDao: WeatherDao
    abstract val repositoryDao: RepositoryDao

    companion object {
        const val DB_NAME = "database.db"
        private var instance: Database? = null
        fun getInstance() = instance ?: throw IllegalStateException("База не создана")
        fun create(context: Context) {
            if (instance == null) {
                instance = Room.databaseBuilder(context, Database::class.java, DB_NAME).build()
            }
        }

    }
}