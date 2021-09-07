package com.mas.weather_kotlin.di

import com.mas.weather_kotlin.di.module.ApiModule
import com.mas.weather_kotlin.di.module.AppModule
import com.mas.weather_kotlin.di.module.CiceroneModule
import com.mas.weather_kotlin.di.module.WeatherModule
import com.mas.weather_kotlin.mvp.presenter.DayInfoPresenter
import com.mas.weather_kotlin.mvp.presenter.MainPresenter
import com.mas.weather_kotlin.mvp.presenter.SettingsPresenter
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.ui.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        AppModule::class,
        CiceroneModule::class,
        WeatherModule::class
    ]
)

interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainPresenter: MainPresenter)
    fun inject(weatherPresenter: WeatherPresenter)
    fun inject(dayInfoPresenter: DayInfoPresenter)
    fun inject(settingsPresenter: SettingsPresenter)
}