package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository

interface IGithubRepositoryCache {
    fun insert(repositories: List<GithubUserRepository>, user: GithubUser)
    fun getByUser(user: GithubUser): List<GithubUserRepository>
}