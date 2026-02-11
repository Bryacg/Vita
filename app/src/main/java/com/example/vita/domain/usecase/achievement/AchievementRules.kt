package com.example.vita.domain.usecase.achievement

object AchievementRules {
    // Definimos los logros "maestros" de la App
    val LOGROS_MAESTROS = listOf(
        Triple("Primeros Pasos", "Completa tu primer reto diario", 1),
        Triple("Gourmet", "Agregaste al menos 3 preferencias alimentarias", 3),
        Triple("Constancia", "Completa 5 retos en total", 5),
        Triple("Nivel Explorador", "Alcanza el nivel 2", 2)
    )
}