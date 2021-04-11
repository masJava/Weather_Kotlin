package com.mas.weather_kotlin.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface UsersInfoView : MvpView {
    fun init()
    fun updateList()
    fun setLogin(text: String)
    fun setAvatar(urlAvatar: String)
}