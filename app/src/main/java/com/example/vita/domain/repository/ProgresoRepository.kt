package com.example.vita.domain.repository

import com.example.vita.domain.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgresoRepository {

    // 1. FUNCIÓN VITAL PARA LA HOME: Permite observar cambios en tiempo real
    fun getProgresoStream(uid: String): Flow<Progress?>

    // 2. Obtención de datos de un solo disparo (usado en el init del ViewModel)
    suspend fun getProgreso(uid: String): Progress?

    // 3. Inserción inicial
    suspend fun insertarProgreso(progreso: Progress)

    /**
     * Incrementa la experiencia acumulada.
     */
    suspend fun agregarXp(uid: String, xp: Int)

    /**
     * Actualiza el nivel cuando el LevelCalculator detecta un cambio de rango.
     */
    suspend fun actualizarNivel(uid: String, nivel: Int)

    /**
     * Actualiza el Índice de Masa Corporal calculado.
     */
    suspend fun actualizarBmi(uid: String, bmi: Double)

    /**
     * Actualiza la racha de días consecutivos.
     */
    suspend fun actualizarRacha(uid: String, dias: Int)

    /**
     * Resetea la racha si el usuario no cumplió sus metas diarias.
     */
    suspend fun resetearRacha(uid: String)
}