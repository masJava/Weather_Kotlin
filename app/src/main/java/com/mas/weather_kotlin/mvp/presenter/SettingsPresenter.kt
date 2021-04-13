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

//class RepoInfoPresenter(private val repo: GithubUserRepository) : MvpPresenter<RepoInfoView>() {
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


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("my", "Settings")
        viewState.setSpinnerPosition(settings.position)
//        viewState.setName(repo.name)
//        viewState.setDescription("Description\n${repo.description}")
//        viewState.setUrl(repo.htmlUrl)
//        viewState.setForkCount("Fork count: ${repo.forks_count}")
    }

//    fun changeSettings(city: String, lat:String,lon:String,position: Int){
//        settings.city = city
//        settings.lat = lat
//        settings.lon = lon
//        settings.position = position
//    }

    fun loadData(city: String): MutableList<CitiesRequestModel>? {
        var cities: MutableList<CitiesRequestModel>? = null
        weather.getCities(city)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        cities?.clear()
                        cities?.addAll(citiesRequest)
                    }
                    Log.d("my", "cities")
                },
                { t -> Log.d("my", t.message.toString()) })
        return cities
    }

    fun goToWeather(){
        router.newRootScreen(screens.weather())
    }


    fun backClick(): Boolean {
        Log.d("my", "settings back")
        router.exit()
        return true
    }
}