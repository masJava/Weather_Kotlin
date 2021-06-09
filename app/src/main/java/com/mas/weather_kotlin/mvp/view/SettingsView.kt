package com.mas.weather_kotlin.mvp.view

import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {
    fun setSpinnerPosition(position: Int)
    fun setSpinnerAdapter(cities: MutableList<CitiesRequestModel>)
    fun setCity(city: String)
    fun setGPSSwitch(gpsKey: Boolean)
    fun setGPSText(location: String)
    fun showToast(text: String)
}