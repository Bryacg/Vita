package com.example.vita.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.vita.R
import java.util.concurrent.TimeUnit

class HydrationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        showNotification(
            title   = "¡Misión de Hidratación! 💧",
            message = "Es hora de beber agua para recuperar estamina y mejorar tu salud."
        )
        scheduleNext()
        return Result.success()
    }

    // Corregido: enqueueUniqueWork evita notificaciones duplicadas
    private fun scheduleNext() {
        val request = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("agua")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "work_agua_daily",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "misiones_importantes"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Misiones Críticas VitaGame",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_apple)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        nm.notify("agua_reminder".hashCode(), notification)
    }
}