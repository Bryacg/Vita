package com.example.vita.domain.model

object GameConfig {
    const val XP_RETO_DIARIO = 80
    const val XP_RETO_SEMANAL = 400
    const val XP_MINIJUEGO_GODOT = 170
    const val MAX_RETOS_DIARIOS = 8
    const val MAX_RETOS_SEMANALES = 5
    // XP por registro de comida según calidad
    const val XP_COMIDA_MUY_SALUDABLE = 20
    const val XP_COMIDA_SALUDABLE     = 15
    const val XP_COMIDA_REGULAR       = 8
    const val XP_COMIDA_POCO_SANO     = 3
    const val XP_COMIDA_EXCESO        = 2   // cuando supera el límite calórico

    // Por encima de este valor por ingesta → forzado a "Poco sano"
    const val LIMITE_CALORIAS_INGESTA = 700
}