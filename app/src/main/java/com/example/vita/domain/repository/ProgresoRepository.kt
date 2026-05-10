package com.example.vita.domain.repository

import com.example.vita.domain.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgresoRepository {

    fun getProgresoStream(uid: String): Flow<Progress?>

    suspend fun getProgreso(uid: String): Progress?
    suspend fun getProgresoPorFecha(uid: String, fecha: Long): Progress?
    suspend fun getTotalXpDeSiempre(uid: String): Int

    suspend fun insertarProgreso(progreso: Progress)
    suspend fun agregarXpHoy(uid: String, xp: Int, fecha: Long)
    suspend fun actualizarNivel(uid: String, nivel: Int)
    suspend fun actualizarBmi(uid: String, bmi: Double)
    suspend fun actualizarRacha(uid: String, dias: Int)
    suspend fun resetearRacha(uid: String)
    suspend fun agregarXp(uid: String, xp: Int)

    // ✅ Registros diarios de los últimos 7 días para el gráfico
    suspend fun getProgresoUltimaSemana(uid: String): List<Progress>
}