package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.view.SettingsView
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class SettingsPresenter() : MvpPresenter<SettingsView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var weather: IOpenWeather

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var settings: SettingsModel

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

    var cities = mutableListOf<CitiesRequestModel>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("my", "Settings")
        if (settings.city.isNotEmpty())
            loadData(settings.city)
        viewState.setSpinnerPosition(settings.position)
        viewState.setCity(settings.city)
    }


    fun loadData(city: String): MutableList<CitiesRequestModel> {
        weather.getCities(city)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        cities.clear()
                        cities.addAll(citiesRequest)

                        viewState.setSpinnerAdapter(cities)
                    }
                    Log.d("my", "cities")
                },
                { t -> Log.d("my", t.message.toString()) })
        return cities
    }

    fun cityToSettings(position: Int) {
        if (position < cities.size) {

            with(cities.get(position)) {
                settings.city =
                    if (local_names?.ru == "") local_names.featureName else local_names?.ru.toString()
                settings.lat = lat.toString()
                settings.lon = lon.toString()
            }
            settings.position = position
        }
    }

    fun goToWeather() {
        router.newRootScreen(screens.weather())
    }


    fun backClick(): Boolean {
        Log.d("my", "settings back")
        router.exit()
        return true
    }
}