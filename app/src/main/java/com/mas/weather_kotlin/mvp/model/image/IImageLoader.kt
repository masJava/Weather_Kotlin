package com.mas.weather_kotlin.mvp.model.image

interface IImageLoader<T> {
    fun load(url: String, container: T)
}