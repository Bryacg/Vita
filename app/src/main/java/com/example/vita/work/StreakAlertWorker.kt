package com.example.vita.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDateTime
import kotlin.time.Duration

//// workers/StreakAlertWorker.kt
//class StreakAlertWorker(
//    appContext: Context,
//    workerParams: WorkerParameters
//) : CoroutineWorker(appContext, workerParams) {
//
//    override suspend fun doWork(): Result {
//        // 1. Verificar si el streak está en riesgo (ej: última actividad fue hace >23h)
//        val progressRepository = // inyecta con Hilt o manual
//        val progress = progressRepository.getCurrentProgress()
//
//        if (progress.isStreakAtRisk()) {  // tu lógica
//            showStreakWarningNotification(applicationContext)
//        }
//
//        // 2. Reprogramar para el próximo día (patrón importante)
//        scheduleNextAlert(applicationContext)
//
//        return Result.success()
//    }
//
//    private fun scheduleNextAlert(context: Context) {
//        val now = LocalDateTime.now()
//        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
//        val delayMillis = Duration.between(now, nextMidnight).toMillis() - (5 * 60 * 1000) // 5 min antes
//
//        val request = OneTimeWorkRequestBuilder<StreakAlertWorker>()
//            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
//            .addTag("streak_alert")
//            .build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork(
//                "streak_alert_daily",
//                ExistingWorkPolicy.REPLACE,
//                request
//            )
//    }
//
//    companion object {
//        fun scheduleInitial(context: Context) {
//            // Llamar una vez al iniciar sesión o al resetear streak
//            scheduleNextAlert(context)
//        }
//    }
//}