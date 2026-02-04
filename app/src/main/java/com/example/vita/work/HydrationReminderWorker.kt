package com.example.vita.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.vita.R
import java.util.concurrent.TimeUnit

class HydrationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Mostrar la notificación
        showNotification(
            title = "¡Misión de Hidratación! 💧",
            message = "Es hora de beber agua para recuperar estamina y mejorar tu salud."
        )

        // 2. Programar automáticamente para el día siguiente (24 horas después)
        // Esto mantiene el ciclo activo hasta que el usuario lo apague en el perfil
        val nextRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("agua")
            .build()

        WorkManager.getInstance(applicationContext).enqueue(nextRequest)

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "misiones_importantes"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Misiones Críticas VitaGame",
                // IMPORTANCE_HIGH hace que la notificación aparezca en pantalla (heads-up) y suene
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarmas de misiones diarias"
                enableLights(true)
                enableVibration(true)
                // Aquí puedes incluso definir un patrón de vibración tipo "pulso"
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Icono de alarma
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridad máxima
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Categoría de alarma
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Usa sonido, vibración y luces por defecto
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}