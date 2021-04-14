package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.Tools
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.view.DayInfoView
import com.mas.weather_kotlin.ui.App
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class DayInfoPresenter(private val day: DailyRestModel) :
    MvpPresenter<DayInfoView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var settings: SettingsModel

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val time = Tools().decodeTime(
            Tools().PATTERN_EEE_D_MMM,
            day.dt,
            GregorianCalendar().timeZone.rawOffset / 1000L
        )
        var maxT = ""
        var minT = ""
        var humidity: String
        var pressure: String
        var rain: String
        with(App.instance.resources) {
            if (day.temp != null) {
                maxT =
                    getString(R.string.wi_direction_up) + " %.1f\u00b0C".format(Math.round(day.temp.max * 10) / 10f)
                minT =
                    getString(R.string.wi_direction_down) + " %.1f\u00b0C".format(Math.round(day.temp.min * 10) / 10f)
            }
            humidity = "${getString(R.string.wi_humidity)} ${day.humidity} %"
            pressure =
                "${getString(R.string.wi_barometer)} ${Math.round(day.pressure / 1.333)} mm Hg"
            rain = "${getString(R.string.wi_umbrella)} %.2f mm/h".format(day.rain)

        }

        viewState.setDay(time)
        viewState.setTitle(settings.city)
        viewState.setTempMax(maxT)
        viewState.setTempMin(minT)
        viewState.setDailyHumidity(humidity)
        viewState.setDailyPressure(pressure)
        viewState.setDailyRain(rain)
        val weatherIcoUrl =
            "https://openweathermap.org/img/wn/%s@2x.png".format(day.weather?.get(0)?.icon)
        viewState.loadWeatherIco(weatherIcoUrl)

    }


    fun backClick(): Boolean {
        Log.d("my", "DayInfoPres")
        router.exit()
        return true
    }
}