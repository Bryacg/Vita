package com.example.vita.data.remote.gemini

import android.util.Log
import com.example.vita.di.RetosApi
import com.example.vita.domain.model.Challenger
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRetosDataSource @Inject constructor(
    @RetosApi private val model: GenerativeModel
) {
    // ✅ Gson vive aquí — capa Data, no en Domain
    private val gson = Gson()

    suspend fun generarRetos(uid: String, nombre: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Genera 8 retos de salud para $nombre en formato JSON.
                    Formato: [{"name": "...", "description": "...", "type": "DIARIO",
                    "targetValue": 5, "currentValue": 0, "deadline": 0, "status": "ACTIVO"}]
                    Devuelve SOLO el array JSON, sin texto adicional.
                """.trimIndent()

                val response = model.generateContent(prompt)
                val rawText = response.text ?: ""
                Log.d("VITA_LOG", "GeminiRetosDataSource respuesta: $rawText")

                val startIndex = rawText.indexOf("[")
                val endIndex = rawText.lastIndexOf("]")
                if (startIndex == -1 || endIndex == -1) {
                    Log.w("VITA_LOG", "GeminiRetosDataSource: JSON no encontrado")
                    return@withContext emptyList()
                }

                val jsonLimpio = rawText.substring(startIndex, endIndex + 1)
                val type = object : TypeToken<List<ChallengerDto>>() {}.type
                val dtos: List<ChallengerDto> = gson.fromJson(jsonLimpio, type)

                // ✅ Mapea DTO → Dominio aquí, no en el UseCase
                dtos.map { dto ->
                    Challenger(
                        id = 0,
                        userId = uid,
                        name = dto.name,
                        description = dto.description,
                        type = dto.type,
                        targetValue = dto.targetValue,
                        currentValue = 0,
                        status = "ACTIVO",
                        deadline = System.currentTimeMillis() + 86_400_000L
                    )
                }
            } catch (e: Exception) {
                Log.e("VITA_LOG", "GeminiRetosDataSource error: ${e.message}")
                emptyList()
            }
        }
}

// DTO privado — solo la capa Data lo conoce
private data class ChallengerDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("type") val type: String = "DIARIO",
    @SerializedName("targetValue") val targetValue: Int = 1,
    @SerializedName("currentValue") val currentValue: Int = 0,
    @SerializedName("deadline") val deadline: Long = 0,
    @SerializedName("status") val status: String = "ACTIVO"
)