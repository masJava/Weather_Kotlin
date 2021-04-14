package com.mas.weather_kotlin.mvp.view

import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {
    fun setSpinnerPosition(position: Int)
    fun setSpinnerAdapter(items: MutableList<CitiesRequestModel>)
    fun setCity(city: String)
//    fun setName(name: String)
//    fun setDescription(description: String)
//    fun setUrl(url: String)
//    fun setForkCount(forkCount: String)
}