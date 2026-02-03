package com.example.vita.domain.usecase.chat

import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.FoodRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class ObtenerPromptNutricionalUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(mensajeUsuario: String): String {
        // 1. Obtenemos el ID del usuario
        val uid = authRepository.getCurrentUserId() ?: return mensajeUsuario

        // 2. Obtenemos los datos de los repositorios
        val user = userRepository.getUserById(uid)
        val profile = profileRepository.getProfileByUserId(uid)
        val preferences = foodRepository.getUserFoodPreferences(uid)

        // 3. Clasificamos las preferencias (Usando los strings de tu UI)
        val likes = preferences.filter { it.second.preferenceType == "Gusta" }
            .joinToString { it.first.name }.ifEmpty { "Ninguno" }

        val dislikes = preferences.filter { it.second.preferenceType == "Disgusta" }
            .joinToString { it.first.name }.ifEmpty { "Ninguno" }

        val allergies = preferences.filter { it.second.preferenceType == "Alérgico" }
            .joinToString { it.first.name }.ifEmpty { "Ninguna" }

        // 4. Construimos el contexto manualmente aquí para evitar errores de parámetros faltantes en 'construirContextoNutricional'
        val contexto = """
            CONTEXTO DEL USUARIO:
            - Nombre: ${user?.name ?: "Usuario"} (Nivel: ${user?.currentLevel ?: 1})
            - Biometría: ${profile?.weight ?: 0}kg, ${profile?.height ?: 0}cm, ${profile?.age ?: 0} años, Género: ${profile?.gender ?: "No especificado"}.
            - Le gusta: $likes.
            - No le gusta: $dislikes.
            - ALERGIAS/RESTRICCIONES: $allergies.
            
            INSTRUCCIONES PARA VITA BOT:
            1. No sugieras alimentos que estén en la lista de ALERGIAS.
            2. Prioriza sus gustos en las recomendaciones.
            3. Responde de forma motivadora y técnica.
        """.trimIndent()

        return """
            $contexto
            
            PREGUNTA ACTUAL DEL USUARIO:
            $mensajeUsuario
        """.trimIndent()
    }
}