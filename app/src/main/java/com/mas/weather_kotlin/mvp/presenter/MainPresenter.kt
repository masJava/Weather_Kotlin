package com.mas.weather_kotlin.mvp.presenter

import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.view.MainView
import com.mas.weather_kotlin.ui.App
import moxy.MvpPresenter
import javax.inject.Inject

class MainPresenter() :
    MvpPresenter<MainView>() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screens: IScreens


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (App.settings.city.isEmpty() || App.settings.lat.isEmpty() || App.settings.lon.isEmpty()) {
            router.replaceScreen(screens.settings())
        } else {
            router.replaceScreen(screens.weather())
        }
    }

    fun backClick() {
        router.exit()
    }

}