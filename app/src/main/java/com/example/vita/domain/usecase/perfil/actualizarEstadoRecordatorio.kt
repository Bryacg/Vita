package com.example.vita.domain.usecase.perfil

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vita.work.HydrationReminderWorker
import com.example.vita.work.WalkReminderWorker  // ✅ nombre corregido
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ManageReminderUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("misiones_prefs", Context.MODE_PRIVATE)

    operator fun invoke(tipo: String, activo: Boolean, hora: Int, minuto: Int) {
        val horaStr = "${hora.toString().padStart(2, '0')}:${minuto.toString().padStart(2, '0')}"
        prefs.edit().apply {
            putBoolean("${tipo}_activo", activo)
            putString("${tipo}_hora", horaStr)
            apply()
        }

        val workManager = WorkManager.getInstance(context)
        val workName    = "work_$tipo"

        if (!activo) {
            workManager.cancelUniqueWork(workName)
            return
        }

        val delay = calcularDelay(hora, minuto)
        val workRequest = when (tipo.lowercase()) {
            "agua"    -> OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            "caminar" -> OneTimeWorkRequestBuilder<WalkReminderWorker>() // ✅ nombre corregido
            else      -> OneTimeWorkRequestBuilder<HydrationReminderWorker>()
        }
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(tipo)
            .build()

        workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun obtenerEstadoGuardado(tipo: String): Pair<Boolean, String> {
        val activo     = prefs.getBoolean("${tipo}_activo", false)
        val horaDefault = if (tipo == "agua") "09:00" else "17:30"
        val hora       = prefs.getString("${tipo}_hora", horaDefault) ?: horaDefault
        return Pair(activo, hora)
    }

    private fun calcularDelay(hora: Int, minuto: Int): Long {
        val hoy        = Calendar.getInstance()
        val programada = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
        }
        if (programada.before(hoy)) programada.add(Calendar.DAY_OF_MONTH, 1)
        return programada.timeInMillis - hoy.timeInMillis
    }
}