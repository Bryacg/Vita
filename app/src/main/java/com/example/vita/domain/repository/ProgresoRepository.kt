package com.example.vita.domain.repository

import com.example.vita.domain.model.Progress

interface ProgresoRepository {
    suspend fun getProgreso(uid: String): Progress?
    suspend fun insertarProgreso(progreso: Progress)
    suspend fun agregarXp(uid: String, xp: Int)
    suspend fun actualizarNivel(uid: String, nivel: Int)
    suspend fun actualizarBmi(uid: String, bmi: Double)
    suspend fun actualizarRacha(uid: String, dias: Int)
    suspend fun resetearRacha(uid: String)
}
