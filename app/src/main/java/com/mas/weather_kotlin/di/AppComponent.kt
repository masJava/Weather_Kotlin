package com.mas.weather_kotlin.di

import com.mas.weather_kotlin.di.module.*
import com.mas.weather_kotlin.mvp.presenter.MainPresenter
import com.mas.weather_kotlin.mvp.presenter.RepoInfoPresenter
import com.mas.weather_kotlin.mvp.presenter.UsersInfoPresenter
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.ui.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        AppModule::class,
        CacheModule::class,
        CiceroneModule::class,
        RepoModule::class,
        UserRepositoryModule::class
    ]
)

interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainPresenter: MainPresenter)
    fun inject(weatherPresenter: WeatherPresenter)
    fun inject(usersInfoPresenter: UsersInfoPresenter)
    fun inject(repoInfoPresenter: RepoInfoPresenter)
}