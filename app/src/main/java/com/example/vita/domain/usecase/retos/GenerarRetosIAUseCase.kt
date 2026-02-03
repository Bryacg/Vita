package com.example.vita.domain.usecase.retos

import com.example.vita.di.RetosApi
import com.example.vita.domain.model.Challenger
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import javax.inject.Inject

class GenerarRetosIAUseCase @Inject constructor(
    @RetosApi private val model: GenerativeModel
) {
    private val gson = Gson()

    suspend operator fun invoke(uid: String, nombre: String): List<Challenger> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Genera 8 retos de salud para $nombre en formato JSON.
                Formato: [{"name": "...", "description": "...", "type": "DIARIO", "targetValue": 5, "currentValue": 0, "deadline": 0, "status": "ACTIVO"}]
                Devuelve SOLO el array JSON.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val rawText = response.text ?: ""
            Log.d("VITA_LOG", "Respuesta IA: $rawText")

            val startIndex = rawText.indexOf("[")
            val endIndex = rawText.lastIndexOf("]")
            if (startIndex == -1 || endIndex == -1) return@withContext emptyList()

            val jsonLimpio = rawText.substring(startIndex, endIndex + 1)
            val type = object : TypeToken<List<Challenger>>() {}.type

            val retos: List<Challenger> = gson.fromJson(jsonLimpio, type)

            // Asignamos el UID a cada reto generado
            retos.map { it.copy(userId = uid) }
        } catch (e: Exception) {
            Log.e("VITA_LOG", "Error en UseCase: ${e.message}")
            emptyList()
        }
    }
}