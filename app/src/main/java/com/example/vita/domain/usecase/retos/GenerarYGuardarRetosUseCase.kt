package com.example.vita.domain.usecase.retos

import android.util.Log
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Decide si genera nuevos retos o devuelve los ya existentes del día.
 *
 * Reglas de negocio:
 *  - Si ya hay retos creados HOY  → devuelve esos, NO genera más.
 *  - Si NO hay retos de hoy       → genera con IA y los persiste.
 */
class GenerarYGuardarRetosUseCase @Inject constructor(
    private val generarRetosIAUseCase: GenerarRetosIAUseCase,
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(uid: String, nombre: String): ResultadoRetos =
        withContext(Dispatchers.IO) {
            try {
                // 1. ¿Ya hay retos generados hoy?
                val retosDeHoy = challengeRepository.getChallengesCreatedToday(uid)

                if (retosDeHoy.isNotEmpty()) {
                    Log.d("VITA_LOG", "Ya existen ${retosDeHoy.size} retos de hoy. No se generan más.")
                    return@withContext ResultadoRetos(
                        retos = retosDeHoy,
                        fueronGeneradosAhora = false
                    )
                }

                // 2. No hay retos hoy → generamos con IA
                Log.d("VITA_LOG", "No hay retos hoy. Generando con IA para $nombre...")
                val retosIA = generarRetosIAUseCase(uid, nombre)

                if (retosIA.isEmpty()) {
                    Log.w("VITA_LOG", "La IA devolvió lista vacía.")
                    return@withContext ResultadoRetos(retos = emptyList(), fueronGeneradosAhora = false)
                }

                // 3. Completamos campos y persistimos
                val retosListos = retosIA.map { reto ->
                    reto.copy(
                        userId = uid,
                        status = "ACTIVO",
                        currentValue = 0,
                        deadline = System.currentTimeMillis() + 86_400_000L,
                        createdAt = System.currentTimeMillis()
                    )
                }

                challengeRepository.insertChallenges(retosListos)
                Log.d("VITA_LOG", "${retosListos.size} retos nuevos guardados.")

                ResultadoRetos(retos = retosListos, fueronGeneradosAhora = true)

            } catch (e: Exception) {
                Log.e("VITA_LOG", "GenerarYGuardarRetosUseCase error: ${e.message}")
                ResultadoRetos(retos = emptyList(), fueronGeneradosAhora = false)
            }
        }
}

// Wrapper para saber si la pantalla debe mostrar un mensaje de "¡Retos nuevos!"
data class ResultadoRetos(
    val retos: List<Challenger>,
    val fueronGeneradosAhora: Boolean
)