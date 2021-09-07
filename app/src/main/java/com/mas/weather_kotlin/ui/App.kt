package com.mas.weather_kotlin.ui

import android.app.Application
import com.mas.weather_kotlin.di.AppComponent
import com.mas.weather_kotlin.di.DaggerAppComponent
import com.mas.weather_kotlin.di.module.AppModule
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel

class App : Application() {

    companion object {
        lateinit var instance: App
        var settings = SettingsModel()
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }


}