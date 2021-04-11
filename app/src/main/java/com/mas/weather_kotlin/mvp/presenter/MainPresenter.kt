package com.mas.weather_kotlin.mvp.presenter

import com.github.terrakok.cicerone.Router
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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        router.replaceScreen(screens.users())
    }

    fun backClick() {
        router.exit()
    }

}