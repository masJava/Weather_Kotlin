package com.mas.weather_kotlin.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface RepoInfoView : MvpView {
    fun setName(name: String)
    fun setDescription(description: String)
    fun setUrl(url: String)
    fun setForkCount(forkCount: String)
}