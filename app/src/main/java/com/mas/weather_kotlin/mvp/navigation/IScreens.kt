package com.mas.weather_kotlin.mvp.navigation

import com.github.terrakok.cicerone.Screen
import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel

interface IScreens {
    fun users(): Screen
    fun userInfo(user: DailyRestModel): Screen
    fun repoInfo(repo: GithubUserRepository): Screen
}