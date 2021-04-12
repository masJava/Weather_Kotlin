package com.mas.weather_kotlin.ui.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.ui.fragment.RepoInfoFragment
import com.mas.weather_kotlin.ui.fragment.DayInfoFragment
import com.mas.weather_kotlin.ui.fragment.WeatherFragment

class AndroidScreens : IScreens {
    override fun users() = FragmentScreen {
        WeatherFragment.newInstance()
    }

    override fun userInfo(day: DailyRestModel) = FragmentScreen {
        DayInfoFragment.newInstance(day)
    }

    override fun repoInfo(repo: GithubUserRepository) = FragmentScreen {
        RepoInfoFragment.newInstance(repo)
    }
}