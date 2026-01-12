package com.example.vita.domain.model

import java.time.LocalDate

/**
 * Modelo de dominio para representar el progreso del usuario.
 * Este modelo es puro (sin anotaciones de Room) y se usa en la capa de dominio.
 */
data class Progress(
    val id: Long ,  // Identificador del registro histórico
    val userId: String,  // Usuario al que pertenece el registro
    val level: Int,    // Nivel del usuario en ese momento.
    val xp: Int,   // Experiencia acumulada.
    val weight: Float,   // Peso corporal registrado.
    val bmi: Float,      // Índice de Masa Corporal calculado.
    val date: Long,      // Fecha del snapshot.
    val streakDays: Int   //Fecha cuando registro el progreso
)
