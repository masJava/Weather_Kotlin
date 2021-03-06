package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.*
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.view.DayInfoView
import com.mas.weather_kotlin.ui.App
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

class DayInfoPresenter(private val day: DailyRestModel) :
    MvpPresenter<DayInfoView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screens: IScreens

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val time = day.dt.toStrTime(
            PATTERN_EEE_D_MMMM,
            GregorianCalendar().timeZone.rawOffset / 1000L
        )
        val humidity: String
        val pressure: String
        val rain: String

        humidity = "${day.humidity}%"
        pressure =
            "${(day.pressure / 1.333).roundToInt()} " + App.instance.getString(R.string.pressure_mm_Hg)

        if (App.settings.percentRain) {
            rain = "${(day.pop * 100).roundToInt()} %"
        } else {
            rain = "%.1f ".format((day.rain+day.snow).round1()) + App.instance.getString(R.string.rain_mm)
        }

        val wind =
            "%s %s ".format(Tools().getWindDirection(day.wind_deg), day.wind_speed.roundToInt()) +
                    App.instance.getString(R.string.wind_m_s)

        val sunrise = day.sunrise.toStrTime(PATTERN_HH_MM, App.settings.timeZone)
        val sunset = day.sunset.toStrTime(PATTERN_HH_MM, App.settings.timeZone)
        val pattern = "%.1f\u00b0C"
        val arrayTemp = listOf<String>(
            pattern.format(day.temp.morn),
            pattern.format(day.temp.day),
            pattern.format(day.temp.eve),
            pattern.format(day.temp.night)
        )
        val arrayFlTemp = listOf<String>(
            pattern.format(day.feels_like.morn),
            pattern.format(day.feels_like.day),
            pattern.format(day.feels_like.eve),
            pattern.format(day.feels_like.night)
        )

        viewState.setDay(time)
        viewState.setTitle(App.settings.city)
        viewState.setTempTable(arrayTemp, arrayFlTemp)
        viewState.setDailyHumidity(humidity)
        viewState.setDailyPressure(pressure)
        viewState.setDailyRain(rain)
        viewState.setDailyWind(wind)
        val weatherIcoId = Tools().getIconId(day.weather.get(0).icon)
        viewState.setWeatherIco(weatherIcoId)
        val background = Tools().getBackgroundGradient(day.weather.get(0).icon)
        viewState.setBackground(background)
        val toolbarColor = Tools().getScrimColor(day.weather.get(0).icon)
        viewState.setToolbarColor(toolbarColor)
        viewState.setSunMoonData(sunrise, sunset)

    }


    fun backClick(): Boolean {
        Log.d("my", "DayInfoPres")
        router.exit()
        return true
    }
}