package com.example.vita.domain.usecase.retos

import com.example.vita.data.remote.gemini.GeminiRetosDataSource
import com.example.vita.domain.model.Challenger
import javax.inject.Inject

/**
 * Solicita retos a la IA.
 * El parseo JSON y la comunicación con Gemini son responsabilidad
 * de GeminiRetosDataSource (capa Data), no de este UseCase.
 */
class GenerarRetosIAUseCase @Inject constructor(
    private val geminiRetosDataSource: GeminiRetosDataSource // ✅ DataSource, no GenerativeModel directo
) {
    suspend operator fun invoke(uid: String, nombre: String): List<Challenger> {
        return geminiRetosDataSource.generarRetos(uid, nombre)
    }
}