package com.example.kotlin.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Flame on 2020/05/25.
 */

object TimeUtils {
    var Format_Year = "yyyy"
    var Format_Month = "yyyy-MM"
    var Format_Day = "yyyy-MM-dd"
    var Format_Time = "yyyy-MM-dd HH:mm:ss"
    var Format_Time_Ext = "yyyy-MM-dd HHmmss"

    private val simpleDateFormat: SimpleDateFormat
        private get() = SimpleDateFormat(Format_Time)

    private fun getSimpleDateFormat(format: String?): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }

    val yearDate: String
        get() = getCurrentDate(Format_Year)
    val monthDate: String
        get() = getCurrentDate(Format_Month)
    val dayDate: String
        get() = getCurrentDate(Format_Day)
    val timeDate: String
        get() = getCurrentDate(Format_Time)

    fun getCurrentDate(format: String?): String {
        return dateToString(Date(), format)
    }

    fun dateToString(date: Date?, format: String?): String {
        return getSimpleDateFormat(format).format(date)
    }

    fun yearToData(string: String?): Date? {
        return stringToDate(string, Format_Year)
    }

    fun monthToData(string: String?): Date? {
        return stringToDate(string, Format_Month)
    }

    fun dayToData(string: String?): Date? {
        return stringToDate(string, Format_Day)
    }

    fun timeToData(string: String?): Date? {
        return stringToDate(string, Format_Time)
    }

    fun stringToDate(dateString: String?, format: String?): Date? {
        var date: Date? = null
        try {
            date = getSimpleDateFormat(format).parse(dateString)
        } catch (e: Exception) {
            println("stringToDate error: dateString($dateString)、format($format)")
        }
        return date
    }
}