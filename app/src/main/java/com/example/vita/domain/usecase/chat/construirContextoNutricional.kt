package com.example.vita.domain.usecase.chat

import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User

fun construirContextoNutricional(
    user: User,
    profile: Profile,
    preferences: List<Pair<Food, FoodPreference>>
): String {
    // CORRECCIÓN: Usar los términos que vienen de la UI ("Gusta", "Disgusta", "Alérgico")
    val likes = preferences.filter { it.second.preferenceType == "Gusta" }
        .joinToString { it.first.name }.ifEmpty { "Ninguno registrado" }

    val dislikes = preferences.filter { it.second.preferenceType == "Disgusta" }
        .joinToString { it.first.name }.ifEmpty { "Ninguno registrado" }

    val allergies = preferences.filter { it.second.preferenceType == "Alérgico" }
        .joinToString { it.first.name }.ifEmpty { "Ninguna" }

    return """
        CONTEXTO DEL USUARIO:
        - Nombre: ${user.name} (Nivel ${user.currentLevel})
        - Biometría: ${profile.weight}kg, ${profile.height}cm, ${profile.age} años, Género: ${profile.gender}.
        - Alimentos que le encantan: $likes.
        - Alimentos que odia: $dislikes.
        - RESTRICCIONES MÉDICAS/ALERGIAS: $allergies.
        
        INSTRUCCIONES PARA EL BOT:
        1. Queda estrictamente PROHIBIDO sugerir alimentos que estén en la lista de Alergias.
        2. Si el usuario pide recomendaciones, prioriza sus gustos.
        3. Ajusta el tono a su nivel (${user.currentLevel}).
    """.trimIndent()
}