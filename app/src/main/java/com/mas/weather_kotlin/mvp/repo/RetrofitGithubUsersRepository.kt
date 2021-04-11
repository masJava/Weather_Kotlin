package com.mas.weather_kotlin.mvp.repo

import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.room.cache.IGithubRepositoryCache
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RetrofitGithubUsersRepository(
    val api: IDataSource,
    val networkStatus: INetworkStatus,
    val repositoryCache: IGithubRepositoryCache
) : IGithubUsersRepo {
    override fun getUsersRepository(user: GithubUser) =
        networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                user.reposUrl.let { url ->
                    api.getUserRepos(url).flatMap { repositories ->
                        Single.fromCallable {
                            repositoryCache.insert(repositories, user)
                            repositories
                        }
                    }
                }
            } else {
                Single.fromCallable {
                    repositoryCache.getByUser(user)
                }
            }
        }.subscribeOn(Schedulers.io())
}