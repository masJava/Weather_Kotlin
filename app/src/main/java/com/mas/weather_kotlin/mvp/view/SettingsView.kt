package com.mas.weather_kotlin.mvp.view

import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {
    fun setSpinnerPosition(position: Int)
    fun setSpinnerAdapter(cities: MutableList<CitiesRequestModel>, firstCall: Boolean)
    fun setCity(city: String)
    fun setTiCityEnable(enableKey: Boolean)
    fun setSwitch(settings: SettingsModel)
    fun setCitySpinnerTextChangedListener(gpsKey: Boolean)
    fun showToast(text: String)
}