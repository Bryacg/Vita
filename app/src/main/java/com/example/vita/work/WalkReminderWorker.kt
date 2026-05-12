package com.example.vita.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
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

        // Reprogramar para mañana a la misma hora
        val nextRequest = OneTimeWorkRequestBuilder<WalkReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("caminar")
            .build()

        WorkManager.getInstance(applicationContext).enqueue(nextRequest)

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId         = "misiones_importantes"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Misiones Críticas VitaGame",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description      = "Alarmas de misiones diarias"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
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

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}