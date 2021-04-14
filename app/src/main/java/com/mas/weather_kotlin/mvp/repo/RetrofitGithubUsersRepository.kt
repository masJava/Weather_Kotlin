package com.mas.weather_kotlin.mvp.repo

import android.util.Log
import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RetrofitGithubUsersRepository(
    val api: IDataSource,
    val networkStatus: INetworkStatus,
) : IGithubUsersRepo {


}