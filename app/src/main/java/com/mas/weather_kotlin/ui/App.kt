package com.mas.weather_kotlin.ui

import android.app.Application
import com.mas.weather_kotlin.di.AppComponent
import com.mas.weather_kotlin.di.DaggerAppComponent
import com.mas.weather_kotlin.di.module.AppModule

class App : Application() {

    companion object {
        lateinit var instance: App
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