package com.mas.weather_kotlin.mvp.model

import com.google.gson.Gson
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import com.mas.weather_kotlin.mvp.model.entity.WidgetData
import com.mas.weather_kotlin.ui.App
import io.reactivex.rxjava3.core.Single
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

internal const val PREF_PREFIX_KEY = "appwidget_"
internal const val PREF_WIDGET_HOURLY = "HOURLY"
internal const val PREF_WIDGET_DAILY = "DAILY"
internal const val PATTERN_HH_MM = "HH:mm"
internal const val PATTERN_FULL_DATE_UPD = "yyyy.MM.dd HH:mm:ss"
internal const val PATTERN_DD_MM = "dd.MM"
internal const val PATTERN_EEE_D_MMM = "EEE, d MMM"
internal const val PATTERN_EEE_D_MMMM = "EEE, d MMMM"
internal const val PATTERN_EEE_D = "EEE, d"

class Tools {





    fun getIconId(iconId: String?) = when (iconId) {
        "01d" -> R.drawable.w01d
        "01n" -> R.drawable.w01n
        "02d" -> R.drawable.w02d
        "02n" -> R.drawable.w02n
        "03d" -> R.drawable.w03dn
        "03n" -> R.drawable.w03dn
        "04d" -> R.drawable.w04dn
        "04n" -> R.drawable.w04dn
        "09d" -> R.drawable.w09dn
        "09n" -> R.drawable.w09dn
        "10d" -> R.drawable.w10d
        "10n" -> R.drawable.w10n
        "11d" -> R.drawable.w11dn
        "11n" -> R.drawable.w11dn
        "13d" -> R.drawable.w13dn
        "13n" -> R.drawable.w13dn
        "50d" -> R.drawable.w50dn
        "50n" -> R.drawable.w50dn
        else -> R.drawable.ic_baseline_error_outline_24
    }

    fun getBackgroundGradient(iconId: String?) = when (iconId) {
        "01d" -> R.drawable.gradient_yellow
        "02d" -> R.drawable.gradient_blue
        "03d", "04d" -> R.drawable.gradient_gray
        "09d", "10d", "11d", "13d", "50d" -> R.drawable.gradient_dark_gray

        "01n", "02n", "03n", "04n" -> R.drawable.gradient_gray
        "09n", "10n", "11n", "13n", "50n" -> R.drawable.gradient_dark_gray
        else -> null
    }

    fun getScrimColor(iconId: String?) = when (iconId) {
        "01d" -> R.color.scrim_yellow
        "02d" -> R.color.scrim_blue
        "03d", "04d" -> R.color.scrim_gray
        "09d", "10d", "11d", "13d", "50d" -> R.color.scrim_dark_gray

        "01n", "02n", "03n", "04n" -> R.color.scrim_gray
        "09n", "10n", "11n", "13n", "50n" -> R.color.scrim_dark_gray
        else -> null
    }

    fun getWindDirection(deg: Int): String {
        var dirString: String
        with(App.instance.resources) {
            if (deg == 0 || deg == 360) {
                dirString = getString(R.string.wind_direction_north)
//                dirString += " " + getString(R.string.wi_direction_down)
            } else if (deg > 0 && deg < 90) {
                dirString = getString(R.string.wind_direction_north_east)
//                dirString += " " + getString(R.string.wi_direction_down_left)
            } else if (deg == 90) {
                dirString = getString(R.string.wind_direction_east)
//                dirString += " " + getString(R.string.wi_direction_left)
            } else if (deg > 90 && deg < 180) {
                dirString = getString(R.string.wind_direction_south_east)
//                dirString += " " + getString(R.string.wi_direction_up_left)
            } else if (deg == 180) {
                dirString = getString(R.string.wind_direction_south)
//                dirString += " " + getString(R.string.wi_direction_up)
            } else if (deg > 180 && deg < 270) {
                dirString = getString(R.string.wind_direction_south_west)
//                dirString += " " + getString(R.string.wi_direction_up_right)
            } else if (deg == 270) {
                dirString = getString(R.string.wind_direction_west)
//                dirString += " " + getString(R.string.wi_direction_right)
            } else {
                dirString = getString(R.string.wind_direction_north_west)
//                dirString += " " + getString(R.string.wi_direction_down_right)
            }
        }
        return dirString
    }

    fun convertDataToWidget(weather: WeatherRequestRestModel): WidgetData {
        val widgetData = WidgetData()
        with(widgetData.current) {
            weather.current.let {
                dt = "Current"
                temp = if (weather.current.temp > 0)
                    "+${weather.current.temp.roundToInt()}"
                else weather.current.temp.roundToInt().toString()
                weatherIcoId = Tools().getIconId(weather.current.weather.get(0).icon)
            }
            weather.hourly.let {
                for (i in 1..3) {
                    val time = it[i].dt.toStrTime(PATTERN_HH_MM, App.settings.timeZone)
                    val temp = if (it[i].temp > 0)
                        "+${it[i].temp.roundToInt()}"
                    else it[i].temp.roundToInt().toString()
                    val weatherIcoId = Tools().getIconId(it[i].weather.get(0).icon)
                    widgetData.hourly[i - 1].apply {
                        dt = time
                        this.temp = temp
                        this.weatherIcoId = weatherIcoId
                    }
                }
            }
            weather.daily.let {
                for (i in 1..3) {
                    val time = it[i].dt.toStrTime(PATTERN_EEE_D, App.settings.timeZone)
                    var temp = ""
                    it[i].temp.day.let { day ->
                        temp = if (day > 0)
                            "+${day.roundToInt()}"
                        else day.roundToInt().toString()
                    }
                    val weatherIcoId = Tools().getIconId(it[i].weather.get(0).icon)
                    widgetData.daily[i - 1].apply {
                        dt = time
                        this.temp = temp
                        this.weatherIcoId = weatherIcoId
                    }
                }
            }


        }
        return widgetData
    }

    fun parseJson(json: String): Single<WeatherRequestRestModel> =
        if (json.isNotBlank()) {
            Single.fromCallable { Gson().fromJson(json, WeatherRequestRestModel::class.java) }
        } else {
            Single.fromCallable { WeatherRequestRestModel() }
        }

}

fun Long.toStrTime(pattern: String?, timeZoneOffset: Long): String {
    val calendar: Calendar = GregorianCalendar()
    val localOffset = calendar.timeZone.rawOffset
    val targetTimeStamp = this * 1000 - localOffset + timeZoneOffset * 1000
    val date = Date(targetTimeStamp)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}

fun Float.round1() = Math.round(this * 10) / 10f
