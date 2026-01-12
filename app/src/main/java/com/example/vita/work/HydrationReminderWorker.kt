package com.example.vita.work

// workers/HydrationReminderWorker.kt
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

import java.util.concurrent.TimeUnit
//
//class HydrationReminderWorker(
//    context: Context,
//    params: WorkerParameters
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        // Aquí muestras la notificación
//        showHydrationNotification(applicationContext)
//
//        return Result.success()
//    }
//
//    private fun showHydrationNotification(context: Context) {
//        // Implementa tu lógica de notificación aquí
//        // Ejemplo: NotificationCompat.Builder...
//        // Canal "hydration_channel"
//        // Título: "¡Hora de hidratarte! 💧"
//        // Texto: "Toma un vaso de agua ahora"
//    }
//
//    companion object {
//        const val TAG = "hydration_reminder"
//        const val INTERVAL_HOURS = 3L  // cada 3 horas
//
//        fun schedule(context: Context) {
//            val constraints = Constraints.Builder()
//                .setRequiresBatteryNotLow(true)     // opcional
//                .setRequiresCharging(false)
//                .build()
//
//            val request = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
//                repeatInterval = INTERVAL_HOURS,
//                repeatIntervalTimeUnit = TimeUnit.HOURS,
//                flexTime = 30, TimeUnit.MINUTES      // ventana de ±30 min
//            )
//                .setConstraints(constraints)
//                .addTag(TAG)
//                .build()
//
//            WorkManager.getInstance(context)
//                .enqueueUniquePeriodicWork(
//                    TAG,
//                    ExistingPeriodicWorkPolicy.KEEP,  // o REPLACE si quieres actualizar
//                    request
//                )
//        }
//
//        fun cancel(context: Context) {
//            WorkManager.getInstance(context).cancelUniqueWork(TAG)
//        }
//    }
//}