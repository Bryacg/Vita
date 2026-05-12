package com.example.vita.core

import java.util.Calendar

object DateTimeUtils {

    /** Retorna el timestamp del inicio del día actual (00:00:00.000) */
    fun getTodayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /** Retorna el timestamp del inicio de hace N días */
    fun getDaysAgoMillis(daysAgo: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}