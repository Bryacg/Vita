package com.example.vita.domain.usecase.retos

import android.util.Log
import com.example.vita.core.DateTimeUtils
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Reglas de negocio:
 *
 * DIARIOS
 *   - Se generan una vez por día, al abrir la app.
 *   - createdAt = hoy 00:00:01
 *   - deadline  = hoy 23:59:59
 *
 * SEMANALES
 *   - Solo se generan si es LUNES y no existen para esta semana.
 *   - createdAt = lunes 00:00:01 de esta semana
 *   - deadline  = domingo 23:59:59 de esta semana
 *   - Si no es lunes, se muestran los ya existentes (o nada si aún no hay).
 */
class GenerarYGuardarRetosUseCase @Inject constructor(
    private val generarRetosIAUseCase: GenerarRetosIAUseCase,
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(uid: String, nombre: String): ResultadoRetos =
        withContext(Dispatchers.IO) {
            try {
                val retosParaMostrar = mutableListOf<Challenger>()
                var fueronGenerados  = false

                // ──────────────────────────────────────────────────────────
                // RETOS DIARIOS
                // ──────────────────────────────────────────────────────────
                val diariosDehoy = challengeRepository.getDailyChallengesDeHoy(uid)

                if (diariosDehoy.isNotEmpty()) {
                    Log.d("VITA_LOG", "Ya hay ${diariosDehoy.size} retos diarios para hoy.")
                    retosParaMostrar.addAll(diariosDehoy)
                } else {
                    Log.d("VITA_LOG", "Generando retos DIARIOS para $nombre…")
                    val nuevos = generarRetosIAUseCase.porTipo(uid, nombre, "DIARIO", cantidad = 4)

                    if (nuevos.isNotEmpty()) {
                        val conDeadline = nuevos.map { reto ->
                            reto.copy(
                                userId       = uid,
                                status       = "ACTIVO",
                                currentValue = 0,
                                createdAt    = DateTimeUtils.getTodayStartMillis(),
                                deadline     = DateTimeUtils.getTodayEndMillis()
                            )
                        }
                        challengeRepository.insertChallenges(conDeadline)
                        retosParaMostrar.addAll(conDeadline)
                        fueronGenerados = true
                        Log.d("VITA_LOG", "${conDeadline.size} retos diarios guardados.")
                    } else {
                        Log.w("VITA_LOG", "La IA no devolvió retos diarios.")
                    }
                }

                // ──────────────────────────────────────────────────────────
                // RETOS SEMANALES
                // ──────────────────────────────────────────────────────────
                val semanalesExistentes = challengeRepository.getSemanalesEstaSemana(uid)

                if (semanalesExistentes.isNotEmpty()) {
                    Log.d("VITA_LOG", "Ya hay ${semanalesExistentes.size} retos semanales esta semana.")
                    retosParaMostrar.addAll(semanalesExistentes)
                } else {
                    if (DateTimeUtils.isMonday()) {
                        Log.d("VITA_LOG", "Es lunes → generando retos SEMANALES para $nombre…")
                        val nuevos = generarRetosIAUseCase.porTipo(uid, nombre, "SEMANAL", cantidad = 4)

                        if (nuevos.isNotEmpty()) {
                            val conDeadline = nuevos.map { reto ->
                                reto.copy(
                                    userId       = uid,
                                    status       = "ACTIVO",
                                    currentValue = 0,
                                    createdAt    = DateTimeUtils.getMondayStartMillis(),
                                    deadline     = DateTimeUtils.getThisSundayEndMillis()
                                )
                            }
                            challengeRepository.insertChallenges(conDeadline)
                            retosParaMostrar.addAll(conDeadline)
                            fueronGenerados = true
                            Log.d("VITA_LOG", "${conDeadline.size} retos semanales guardados hasta el domingo.")
                        } else {
                            Log.w("VITA_LOG", "La IA no devolvió retos semanales.")
                        }
                    } else {
                        Log.d("VITA_LOG", "No es lunes. Sin retos semanales hasta el próximo lunes.")
                    }
                }

                ResultadoRetos(
                    retos                = retosParaMostrar,
                    fueronGeneradosAhora = fueronGenerados
                )

            } catch (e: Exception) {
                Log.e("VITA_LOG", "GenerarYGuardarRetosUseCase error: ${e.message}")
                ResultadoRetos(retos = emptyList(), fueronGeneradosAhora = false)
            }
        }
}

data class ResultadoRetos(
    val retos: List<Challenger>,
    val fueronGeneradosAhora: Boolean
)