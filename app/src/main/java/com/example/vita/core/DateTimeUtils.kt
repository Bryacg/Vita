package com.example.vita.core

import java.util.Calendar

object DateTimeUtils {

    /** Inicio del día actual (00:00:00) — usado para progreso */
    fun getTodayMillis(): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /** Inicio del día actual (00:00:01) — createdAt de retos diarios */
    fun getTodayStartMillis(): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /** Fin del día actual (23:59:59) — deadline de retos diarios */
    fun getTodayEndMillis(): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    /** true si hoy es lunes */
    fun isMonday(): Boolean =
        Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY

    /**
     * Lunes de esta semana a las 00:00:01
     * Es el createdAt y el startOfWeek para buscar retos semanales
     */
    fun getMondayStartMillis(): Long =
        Calendar.getInstance().apply {
            val offset = when (get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY    ->  0
                Calendar.TUESDAY   -> -1
                Calendar.WEDNESDAY -> -2
                Calendar.THURSDAY  -> -3
                Calendar.FRIDAY    -> -4
                Calendar.SATURDAY  -> -5
                Calendar.SUNDAY    -> -6
                else               ->  0
            }
            add(Calendar.DAY_OF_YEAR, offset)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /**
     * Domingo de esta semana a las 23:59:59
     * Es el deadline de retos semanales
     */
    fun getThisSundayEndMillis(): Long =
        Calendar.getInstance().apply {
            val daysUntilSunday = when (get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY    -> 6
                Calendar.TUESDAY   -> 5
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY  -> 3
                Calendar.FRIDAY    -> 2
                Calendar.SATURDAY  -> 1
                Calendar.SUNDAY    -> 0
                else               -> 0
            }
            add(Calendar.DAY_OF_YEAR, daysUntilSunday)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    /** Inicio de hace N días (para racha) */
    fun getDaysAgoMillis(daysAgo: Int): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}