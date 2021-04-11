package com.mas.weather_kotlin.di.module

import com.mas.weather_kotlin.ui.App
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: App) {
    @Provides
    fun app(): App = app


}