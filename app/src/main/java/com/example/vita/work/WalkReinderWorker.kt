package com.example.vita.work

//import android.content.Context
//import androidx.work.*
//import java.time.LocalDateTime
//import java.util.concurrent.TimeUnit
//import kotlin.time.Duration
//
//
//// workers/WalkReminderWorker.kt
//class WalkReminderWorker(
//    context: Context,
//    params: WorkerParameters
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        // Mostrar notificación
//        showWalkReminderNotification(applicationContext)
//
//        // Reprogramar para mañana a la misma hora
//        scheduleNext(context)
//
//        return Result.success()
//    }
//
//    private fun scheduleNext(context: Context) {
//        // Ejemplo: hora guardada por el usuario en DataStore o Room (ej: 18:30)
//        val preferredHour = 18
//        val preferredMinute = 30
//
//        val now = LocalDateTime.now()
//        var next = now.withHour(preferredHour).withMinute(preferredMinute).withSecond(0)
//
//        if (now.isAfter(next)) {
//            next = next.plusDays(1)
//        }
//
//        val delay = Duration.between(now, next).toMillis()
//
//        val request = OneTimeWorkRequestBuilder<WalkReminderWorker>()
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//            .addTag("walk_reminder")
//            .build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork(
//                "walk_reminder_daily",
//                ExistingWorkPolicy.REPLACE,
//                request
//            )
//    }
//
//    companion object {
//        fun scheduleOrUpdate(context: Context, hour: Int, minute: Int) {
//            // Llamar cuando el usuario cambie la hora en Profile/Settings
//            scheduleNext(context) // la función interna recalcula
//        }
//    }
//}