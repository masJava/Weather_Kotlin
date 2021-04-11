package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.GithubUser

interface IGithubUsersCache {
    fun insert(users: List<GithubUser>)
    fun getAll(): List<GithubUser>
}