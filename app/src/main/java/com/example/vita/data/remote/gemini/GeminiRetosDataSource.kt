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
    private val gson = Gson()

    /**
     * Genera retos de un tipo específico ("DIARIO" o "SEMANAL").
     * El campo `deadline` se deja en 0 — el UseCase asigna el valor correcto.
     */
    suspend fun generarRetosPorTipo(
        uid: String,
        nombre: String,
        tipo: String,
        cantidad: Int = 4
    ): List<Challenger> = withContext(Dispatchers.IO) {
        try {
            val descripcion = if (tipo == "SEMANAL")
                "semanales (para completar durante toda la semana, con objetivos más altos)"
            else
                "diarios (para completar en un solo día)"

            val prompt = """
                Genera exactamente $cantidad retos de salud $descripcion para $nombre.
                Responde SOLO con un array JSON, sin texto adicional ni bloques de código.
                El campo "type" debe ser exactamente "$tipo".
                Formato: [{"name":"...","description":"...","type":"$tipo","targetValue":5,"currentValue":0,"deadline":0,"status":"ACTIVO"}]
            """.trimIndent()

            val response  = model.generateContent(prompt)
            val rawText   = response.text ?: ""
            Log.d("VITA_LOG", "GeminiRetos [$tipo] respuesta: $rawText")

            val start = rawText.indexOf("[")
            val end   = rawText.lastIndexOf("]")
            if (start == -1 || end == -1) {
                Log.w("VITA_LOG", "GeminiRetos [$tipo]: JSON no encontrado")
                return@withContext emptyList()
            }

            val jsonLimpio = rawText.substring(start, end + 1)
            val listType   = object : TypeToken<List<ChallengerDto>>() {}.type
            val dtos: List<ChallengerDto> = gson.fromJson(jsonLimpio, listType)

            dtos.map { dto ->
                Challenger(
                    id           = 0,
                    userId       = uid,
                    name         = dto.name,
                    description  = dto.description,
                    type         = tipo,              // forzamos el tipo correcto
                    targetValue  = dto.targetValue,
                    currentValue = 0,
                    status       = "ACTIVO",
                    deadline     = 0                  // el UseCase asigna el deadline real
                )
            }
        } catch (e: Exception) {
            Log.e("VITA_LOG", "GeminiRetosDataSource [$tipo] error: ${e.message}")
            emptyList()
        }
    }

    // Método legacy — genera diarios por defecto
    suspend fun generarRetos(uid: String, nombre: String): List<Challenger> =
        generarRetosPorTipo(uid, nombre, "DIARIO", 8)
}

private data class ChallengerDto(
    @SerializedName("name")         val name: String        = "",
    @SerializedName("description")  val description: String = "",
    @SerializedName("type")         val type: String        = "DIARIO",
    @SerializedName("targetValue")  val targetValue: Int    = 1,
    @SerializedName("currentValue") val currentValue: Int   = 0,
    @SerializedName("deadline")     val deadline: Long      = 0,
    @SerializedName("status")       val status: String      = "ACTIVO"
)