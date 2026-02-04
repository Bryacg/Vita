package com.example.vita.domain.repository

import com.example.vita.domain.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgresoRepository {

    // --- FLUJOS REACTIVOS (HOME) ---
    fun getProgresoStream(uid: String): Flow<Progress?>

    // --- CONSULTAS ---
    suspend fun getProgreso(uid: String): Progress?
    suspend fun getProgresoPorFecha(uid: String, fecha: Long): Progress?
    suspend fun getTotalXpDeSiempre(uid: String): Int

    // --- OPERACIONES DE ACTUALIZACIÓN ---
    suspend fun insertarProgreso(progreso: Progress)

    // Esta es la que usaremos en el UseCase para el control diario
    suspend fun agregarXpHoy(uid: String, xp: Int, fecha: Long)

    suspend fun actualizarNivel(uid: String, nivel: Int)
    suspend fun actualizarBmi(uid: String, bmi: Double)
    suspend fun actualizarRacha(uid: String, dias: Int)
    suspend fun resetearRacha(uid: String)

    // Opcional: Puedes mantener esta si tienes otras funciones que no dependen de la fecha
    // pero lo ideal es migrar todo a agregarXpHoy
    suspend fun agregarXp(uid: String, xp: Int)
}