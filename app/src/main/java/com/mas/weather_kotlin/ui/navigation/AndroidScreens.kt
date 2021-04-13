package com.mas.weather_kotlin.ui.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.ui.fragment.SettingsFragment
import com.mas.weather_kotlin.ui.fragment.DayInfoFragment
import com.mas.weather_kotlin.ui.fragment.WeatherFragment

class AndroidScreens : IScreens {
    override fun weather() = FragmentScreen {
        WeatherFragment.newInstance()
    }

    override fun dayInfo(dayWeather: DailyRestModel) = FragmentScreen {
        DayInfoFragment.newInstance(dayWeather)
    }

    override fun settings() = FragmentScreen {
        SettingsFragment.newInstance()
    }
}