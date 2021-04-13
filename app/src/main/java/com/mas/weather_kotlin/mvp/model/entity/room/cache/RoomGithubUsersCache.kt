package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import com.mas.weather_kotlin.mvp.model.entity.any.Rain
import com.mas.weather_kotlin.mvp.model.entity.any.Snow
import com.mas.weather_kotlin.mvp.model.entity.any.WeatherRestModel
import com.mas.weather_kotlin.mvp.model.entity.current.CurrentRestModel
import com.mas.weather_kotlin.mvp.model.entity.room.RoomWeather
import com.mas.weather_kotlin.mvp.model.entity.room.db.Database

class RoomGithubUsersCache(val db: Database) : IGithubUsersCache {
    override fun insert(weather: WeatherRequestRestModel) {
        with(weather) {
            val id = "${lat.toString()}${lon.toString()}"
            val roomWeather = RoomWeather(
                id,
                timezone_offset,
                current?.dt ?: 0L,
                current?.sunrise ?: 0L,
                current?.sunset ?: 0L,
                current?.temp ?: 0f,
                current?.pressure ?: 0,
                current?.humidity ?: 0,
                current?.wind_speed ?: 0f,
                current?.wind_deg ?: 0,
                current?.weather?.get(0)?.icon ?: "",
                current?.rain?.rain ?: 0f,
                current?.snow?.snow ?: 0f
            )

            db.weatherDao.insert(roomWeather)
        }
    }

    override fun getWeather(id: String): WeatherRequestRestModel {
        val roomWeather = db.weatherDao.findById(id)

        return WeatherRequestRestModel(
            "",
            0f,
            0f,
            "",
            roomWeather?.timezone_offset ?: 0L,
            CurrentRestModel(
                roomWeather?.dt ?: 0L,
                roomWeather?.sunrise ?: 0L,
                roomWeather?.sunset ?: 0L,
                roomWeather?.temp ?: 0f,
                0f,
                roomWeather?.pressure ?: 0,
                roomWeather?.humidity ?: 0,
                0f,
                0f,
                0,
                roomWeather?.wind_speed ?: 0f,
                roomWeather?.wind_deg ?: 0,
                listOf(
                    WeatherRestModel(
                        0,
                        "",
                        "",
                        roomWeather?.weatherIco ?: ""
                    )
                ),
                Rain(roomWeather?.rain ?: 0f),
                Snow(roomWeather?.snow ?: 0f),
            ),
            null,
            null

        )
    }
}
