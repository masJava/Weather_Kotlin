package com.mas.weather_kotlin.mvp.model

import com.mas.weather_kotlin.R
import java.text.SimpleDateFormat
import java.util.*

class Tools {

    val PATTERN_HH_MM = "HH:mm"
    val PATTERN_FULL_DATE_UPD = "yyyy.MM.dd HH:mm:ss"
    val PATTERN_DD_MM = "dd.MM"
    val PATTERN_EEE_D_MMM = "EEE, d MMM"

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
}

fun Long.toStrTime(pattern: String?, timeZoneOffset: Long) :String {
    val calendar: Calendar = GregorianCalendar()
    val localOffset = calendar.timeZone.rawOffset
    val targetTimeStamp = this * 1000 - localOffset + timeZoneOffset * 1000
    val date = Date(targetTimeStamp)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}

fun Float.round1() = Math.round(this * 10) / 10f
