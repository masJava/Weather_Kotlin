package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import io.reactivex.rxjava3.core.Single

interface IGithubUsersRepo {
    fun getUsersRepository(user: GithubUser): Single<List<GithubUserRepository>>
}