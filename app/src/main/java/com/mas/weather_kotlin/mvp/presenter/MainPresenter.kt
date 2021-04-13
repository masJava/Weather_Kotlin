package com.mas.weather_kotlin.mvp.presenter

import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.view.MainView
import moxy.MvpPresenter
import javax.inject.Inject

class MainPresenter() :
    MvpPresenter<MainView>() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var settings: SettingsModel

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (settings.city.isEmpty() || settings.lat.isEmpty() || settings.lon.isEmpty()) {
            router.replaceScreen(screens.settings())
        } else {
            router.replaceScreen(screens.weather())
        }
    }

    fun backClick() {
        router.exit()
    }

}