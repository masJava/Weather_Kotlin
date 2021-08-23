package com.mas.weather_kotlin.mvp.model.gps

import com.mas.weather_kotlin.mvp.model.entity.GpsLocation

interface IGPSLocation {
    fun getGPSLocation(): GpsLocation
}