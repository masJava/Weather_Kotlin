package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.Tools
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.model.entity.current.CurrentRestModel
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.model.entity.hourly.HourlyRestModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.presenter.list.IDailyListPresenter
import com.mas.weather_kotlin.mvp.presenter.list.IHourlyListPresenter
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.mvp.view.list.IDailyItemView
import com.mas.weather_kotlin.mvp.view.list.IHourItemView
import com.mas.weather_kotlin.ui.App
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class WeatherPresenter() : MvpPresenter<WeatherView>() {

    @Inject
    lateinit var weather: IOpenWeather

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var settings: SettingsModel

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler


    class DailyListPresenter : IDailyListPresenter {
        val dailyWeather = mutableListOf<DailyRestModel>()
        var timeZone = 0L
        override var itemClickListener: ((IDailyItemView) -> Unit)? = null

        override fun bindView(view: IDailyItemView) {
            val day = dailyWeather[view.pos]
            val time = Tools().decodeTime(Tools().PATTERN_EEE_D_MMM, day.dt, timeZone)
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

            view.setDay(time)
            view.setTempMax(maxT)
            view.setTempMin(minT)
            view.setDailyHumidity(humidity)
            view.setDailyPressure(pressure)
            view.setDailyRain(rain)
            val weatherIcoUrl =
                "https://openweathermap.org/img/wn/%s@2x.png".format(day.weather?.get(0)?.icon)
            view.loadWeatherIco(weatherIcoUrl)
        }

        override fun getCount() = dailyWeather.size
    }

    class HourlyListPresenter : IHourlyListPresenter {
        val hourlyWeather = mutableListOf<HourlyRestModel>()
        var timeZone = 0L
        override var itemClickListener: ((IHourItemView) -> Unit)? = null

        override fun bindView(view: IHourItemView) {
            val hour = hourlyWeather[view.pos]
            var time = Tools().decodeTime(Tools().PATTERN_HH_MM, hour.dt, timeZone)
            if (time.equals("00:00")) {
                time = Tools().decodeTime(Tools().PATTERN_DD_MM, hour.dt, timeZone) + "\n" + time
            } else time = "\n" + time
            view.setHour(time)

            val rain = if (hour.rain != null) hour.rain.rain.toString() else ""
            view.setRainfall(rain)

            view.setTemp((Math.round(hour.temp * 10) / 10f).toString())

            val weatherIcoUrl =
                "https://openweathermap.org/img/wn/%s@2x.png".format(hour.weather?.get(0)?.icon)
            view.loadWeatherIco(weatherIcoUrl)
        }

        override fun getCount() = hourlyWeather.size
    }

    var currentWeather = CurrentRestModel()
    var hourlyListPresenter = HourlyListPresenter()
    var dailyListPresenter = DailyListPresenter()
    var timeZone = 0L

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
        viewState.initRV()

        dailyListPresenter.itemClickListener = { view ->
            val day = dailyListPresenter.dailyWeather[view.pos]
            Log.d("my", day.temp.toString())
            router.navigateTo(screens.dayInfo(day))
        }
    }

    private fun loadData() {
        weather.getWeather(settings.lat, settings.lon)
            .observeOn(uiScheduler)
            .subscribe(
                { weather ->
                    if (weather != null) {
                        timeZone = weather.timezone_offset
                        if (weather.current != null) {
                            currentWeather = weather.current
                            currentWeatherUpdate()
                        }
                        if (weather.hourly != null) {
                            viewState.hintVisible(true)
                            hourlyListPresenter.hourlyWeather.clear()
                            hourlyListPresenter.hourlyWeather.addAll(weather.hourly)
                            hourlyListPresenter.timeZone = timeZone
                            viewState.updateHourlyList()
                        } else {
                            viewState.hintVisible(false)
                        }
                        if (weather.daily != null) {
                            dailyListPresenter.dailyWeather.clear()
                            dailyListPresenter.dailyWeather.addAll(weather.daily)
                            dailyListPresenter.timeZone = timeZone
                            viewState.updateDailyList()
                        }
                    }
                    Log.d("my", "weather")
                },
                { t -> Log.d("my", t.message.toString()) })


    }

    private fun currentWeatherUpdate() {
        var temp: String
        var hum: String
        var sunrise = ""
        var sunset = ""
        var wind: String
        var press: String
        var update: String
        var weatherIcoUrl: String
        with(App.instance.resources) {
            temp =
                "${getString(R.string.wi_thermometer)} %s \u00b0C".format(Math.round(currentWeather.temp * 10) / 10f)
            hum = "${getString(R.string.wi_humidity)} ${currentWeather.humidity} %"
            press =
                "${getString(R.string.wi_barometer)} ${Math.round(currentWeather.pressure / 1.333)} mm Hg"
            wind = "${getString(R.string.wi_strong_wind)} %s %s m/s"
                .format(
                    getWindDirection(currentWeather.wind_deg),
                    Math.round(currentWeather.wind_speed)
                )

            if (currentWeather.sunrise > 0) {
                sunrise = getString(R.string.wi_sunrise) + " " +
                        Tools().decodeTime(
                            Tools().PATTERN_HH_MM,
                            currentWeather.sunrise,
                            timeZone
                        )
            }

            if (currentWeather.sunset > 0) {
                sunset = getString(R.string.wi_sunset) + " " +
                        Tools().decodeTime(
                            Tools().PATTERN_HH_MM,
                            currentWeather.sunset,
                            timeZone
                        )
            }

            update = "Updated: " + Tools().decodeTime(
                Tools().PATTERN_FULL_DATE_UPD, currentWeather.dt,
                GregorianCalendar().timeZone.rawOffset / 1000L
            )

            weatherIcoUrl =
                "https://openweathermap.org/img/wn/%s@2x.png".format(currentWeather.weather?.get(0)?.icon)
        }



        viewState.setCurrentCityName(settings.city)
        viewState.setUpdate(update)
        viewState.setCurrentTemp(temp)
        viewState.setCurrentHum(hum)
        viewState.setCurrentPress(press)
        viewState.setCurrentSunrise(sunrise)
        viewState.setCurrentSunset(sunset)
        viewState.setCurrentWind(wind)
        viewState.setCurrentWeatherIco(weatherIcoUrl)
    }

    fun backClick(): Boolean {
        Log.d("my", "weather back")
        router.exit()
        return true
    }

    fun getWindDirection(deg: Int): String {
        var dirString: String
        with(App.instance.resources) {
            if (deg == 0 || deg == 360) {
                dirString = getString(R.string.wind_direction_north)
                dirString += " " + getString(R.string.wi_direction_down)
            } else if (deg > 0 && deg < 90) {
                dirString = getString(R.string.wind_direction_north_east)
                dirString += " " + getString(R.string.wi_direction_down_left)
            } else if (deg == 90) {
                dirString = getString(R.string.wind_direction_east)
                dirString += " " + getString(R.string.wi_direction_left)
            } else if (deg > 90 && deg < 180) {
                dirString = getString(R.string.wind_direction_south_east)
                dirString += " " + getString(R.string.wi_direction_up_left)
            } else if (deg == 180) {
                dirString = getString(R.string.wind_direction_south)
                dirString += " " + getString(R.string.wi_direction_up)
            } else if (deg > 180 && deg < 270) {
                dirString = getString(R.string.wind_direction_south_west)
                dirString += " " + getString(R.string.wi_direction_up_right)
            } else if (deg == 270) {
                dirString = getString(R.string.wind_direction_west)
                dirString += " " + getString(R.string.wi_direction_right)
            } else {
                dirString = getString(R.string.wind_direction_north_west)
                dirString += " " + getString(R.string.wi_direction_down_right)
            }
        }
        return dirString
    }

    fun navigateSettings() {
        router.navigateTo(screens.settings())
    }
}