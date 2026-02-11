package com.example.vita.domain.usecase.godot

import android.util.Log
import com.example.vita.data.remote.godot.GameResultBuffer
import com.example.vita.domain.repository.AchievementRepository
import javax.inject.Inject

class ProcessGameResultUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {
    suspend operator fun invoke() {
        val resultado = GameResultBuffer.ultimoResultado
        if (resultado == "GANASTE") {
            // Aquí desbloqueas el logro en la DB
            // achievementRepository.unlockAchievement(...)
            Log.d("Vita", "¡Felicidades! Logro desbloqueado por ganar en Godot")
        }
        // Limpiar después de procesar
        GameResultBuffer.ultimoResultado = null
    }
}