package com.mas.weather_kotlin.mvp.model

import java.text.SimpleDateFormat
import java.util.*

class Tools {

    val PATTERN_HH_MM = "HH:mm"
    val PATTERN_FULL_DATE_UPD = "yyyy.MM.dd HH:mm:ss"
    val PATTERN_DD_MM = "dd.MM"
    val PATTERN_EEE_D_MMM = "EEE, d MMM"

    fun decodeTime(pattern: String?, timestampSeconds: Long, timeZoneOffset: Long): String {
        val calendar: Calendar = GregorianCalendar()
        val localOffset = calendar.timeZone.rawOffset
        val targetTimeStamp = timestampSeconds * 1000 - localOffset + timeZoneOffset * 1000
        val date = Date(targetTimeStamp)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }
}