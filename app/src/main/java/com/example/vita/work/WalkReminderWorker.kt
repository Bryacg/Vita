package com.example.vita.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class WalkReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        showNotification(
            title   = "¡Misión de Exploración! 👟",
            message = "Es un gran momento para salir a caminar. ¡Suma pasos para tu nivel!"
        )
        scheduleNext()
        return Result.success()
    }

    // Corregido: enqueueUniqueWork evita duplicados
    private fun scheduleNext() {
        val request = OneTimeWorkRequestBuilder<WalkReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("caminar")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "work_caminar_daily",
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        nm.notify("caminar_reminder".hashCode(), notification)
    }
}