package com.mas.weather_kotlin.ui.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.ui.fragment.RepoInfoFragment
import com.mas.weather_kotlin.ui.fragment.UserInfoFragment
import com.mas.weather_kotlin.ui.fragment.WeatherFragment

class AndroidScreens : IScreens {
    override fun users() = FragmentScreen {
        WeatherFragment.newInstance()
    }

    override fun userInfo(user: GithubUser) = FragmentScreen {
        UserInfoFragment.newInstance(user)
    }

    override fun repoInfo(repo: GithubUserRepository) = FragmentScreen {
        RepoInfoFragment.newInstance(repo)
    }
}