package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.repository.ChatRepository      // ✅ repositorio, no UseCase de Chat
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Genera retos usando el contexto nutricional del usuario.
 *
 * CORRECCIÓN: Inyecta ChatRepository directamente en lugar de
 * EnviarMensajeChatUseCase (que es un UseCase de otro dominio).
 * Los UseCases de distintos dominios no deben conocerse entre sí.
 *
 * NOTA: Este UseCase aún no está conectado a ningún ViewModel.
 */
class ObtenerRetosNutricionalUseCase @Inject constructor(
    private val chatRepository: ChatRepository,     // ✅ repositorio, no UseCase
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(uid: String): List<Challenger> {
        val user = userRepository.getUserById(uid)
        val profile = profileRepository.getProfileByUserId(uid)

        val prompt = """
            Eres un coach de salud. Genera 8 retos de bienestar para ${user?.name}.
            Datos: Peso ${profile?.weight}kg, Altura ${profile?.height}cm.
            Responde ÚNICAMENTE con JSON: [{"name":"...","description":"...","type":"DIARIO","targetValue":8,"status":"ACTIVO"}]
        """.trimIndent()

        return try {
            val respuesta = chatRepository.sendMessage(
                ChatMessage(sender = "user", content = prompt)
            )
            parsearRetosIA(respuesta.content, uid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parsearRetosIA(json: String, uid: String): List<Challenger> {
        // TODO: implementar parseo con Gson en capa Data
        return emptyList()
    }
}