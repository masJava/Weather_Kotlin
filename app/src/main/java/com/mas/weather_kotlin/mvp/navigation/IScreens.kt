package com.mas.weather_kotlin.mvp.navigation

import com.github.terrakok.cicerone.Screen
import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository

interface IScreens {
    fun users(): Screen
    fun userInfo(user: GithubUser): Screen
    fun repoInfo(repo: GithubUserRepository): Screen
}