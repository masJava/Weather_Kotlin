package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.view.SettingsView
import com.mas.weather_kotlin.ui.App
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


//    @Inject
//    lateinit var fusedLocationClient: FusedLocationProviderClient

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

//    var cities = mutableListOf<CitiesRequestModel>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("my", "Settings")
        if (App.settings.city.isNotEmpty())
            loadCityList(App.settings.city)
//        viewState.setSpinnerPosition(settings.position)
        viewState.setCity(App.settings.city)
        viewState.setSwitch(App.settings)
        viewState.setCitySpinnerTextChangedListener(App.settings.gpsKey)
    }

    fun loadCityList(city: String) {
        weather.getCities(city)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        if (citiesRequest.isEmpty()) {
//                            viewState.showToast("Unknown city")
                            viewState.setCityReportLogo(false)
                        } else {
                            if (cityCheck(city, citiesRequest[0])
                            ) {
                                viewState.setCityReportLogo(true)
                                cityToSettings(citiesRequest[0])
                            }
                        }
//                        cities.clear()
//                        cities.addAll(citiesRequest)
                        //viewState.setSpinnerAdapter(cities, fistCall)
                    }
                    Log.d("my", "cities")
                },
                { t -> Log.d("my", t.message.toString()) })
    }

    private fun cityCheck(city: String, citiesRequest: CitiesRequestModel): Boolean {
        return citiesRequest.name.toLowerCase().equals(city.toLowerCase()) ||
                citiesRequest.local_names?.featureName?.toLowerCase().equals(city.toLowerCase()) ||
                citiesRequest.local_names?.ru?.toLowerCase().equals(city.toLowerCase())
    }

    fun cityToSettings(citiesRequest: CitiesRequestModel) {
        with(citiesRequest) {
            App.settings.city =
                if (local_names?.ru == "") local_names.featureName else local_names?.ru.toString()
            App.settings.lat = lat.toString()
            App.settings.lon = lon.toString()
        }
    }

    fun gpsSettingsChange(isChecked: Boolean) {
        App.settings.gpsKey = isChecked
        viewState.setTiCityEnable(!isChecked)
        viewState.setCitySpinnerTextChangedListener(isChecked)

    }


    fun backClick(): Boolean {
        Log.d("my", "settings back")
        if ((App.settings.city.isEmpty() || App.settings.lat.isEmpty() || App.settings.lon.isEmpty()) && !App.settings.gpsKey) {
            router.exit()
        } else {
            router.newRootScreen(screens.weather())
        }
        return true
    }

    fun percentSettingsChange(checked: Boolean) {
        App.settings.percentRain = checked
    }
}