package com.example.vita.domain.usecase.retos

import com.example.vita.data.remote.gemini.GeminiRetosDataSource
import com.example.vita.domain.model.Challenger
import javax.inject.Inject

class GenerarRetosIAUseCase @Inject constructor(
    private val geminiRetosDataSource: GeminiRetosDataSource
) {
    // Uso general (legacy)
    suspend operator fun invoke(uid: String, nombre: String): List<Challenger> =
        geminiRetosDataSource.generarRetos(uid, nombre)

    // Uso por tipo específico
    suspend fun porTipo(
        uid: String,
        nombre: String,
        tipo: String,
        cantidad: Int = 4
    ): List<Challenger> =
        geminiRetosDataSource.generarRetosPorTipo(uid, nombre, tipo, cantidad)
}