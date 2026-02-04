package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.ChatMessage // Importante añadir esto
import com.example.vita.domain.usecase.chat.EnviarMensajeChatUseCase
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.repository.ProfileRepository
import javax.inject.Inject

class ObtenerRetosNutricionalUseCase @Inject constructor(
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(uid: String): List<Challenger> {
        val user = userRepository.getUserById(uid)
        val profile = profileRepository.getProfileByUserId(uid)

        val prompt = """
            Eres un coach de salud. Genera 8 retos de bienestar para el usuario ${user?.name}.
            Datos: Peso ${profile?.weight}kg, Altura ${profile?.height}cm.
            
            REGLAS:
            1. Genera exactamente 3 retos tipo 'DIARIO' y 5 retos tipo 'SEMANAL'.
            2. Responde ÚNICAMENTE con un listado en formato JSON siguiendo esta estructura exacta:
            [
              {
                "name": "Nombre",
                "description": "Descripción corta",
                "type": "DIARIO",
                "targetValue": 8,
                "status": "ACTIVE"
              }
            ]
        """.trimIndent()

        return try {
            // CORRECCIÓN: Llamamos al UseCase directamente pasándole un ChatMessage
            // Esto activa el 'operator fun invoke' de EnviarMensajeChatUseCase
            val respuestaIA = enviarMensajeChatUseCase(
                ChatMessage(sender = "user", content = prompt)
            )
            println("DEBUG_IA_RESPONSE: ${respuestaIA.content}")
            // Usamos respuestaIA.content que contiene el String del JSON generado por Gemini
            parsearRetosIA(respuestaIA.content, uid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parsearRetosIA(json: String, uid: String): List<Challenger> {
        // Por ahora, retornamos una lista vacía para evitar errores de compilación.
        // ¿Te gustaría que implementemos aquí el código con GSON para procesar el JSON real?
        return emptyList()
    }
}