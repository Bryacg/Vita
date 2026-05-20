package com.example.vita.domain.usecase.progreso

import com.example.vita.core.DateTimeUtils
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Devuelve el progreso del usuario.
 * Si no existe (primer inicio), lo crea con valores en 0.
 * SEGURIDAD: antes de insertar cualquier registro, garantiza que
 * UserEntity ya existe en Room para evitar FK constraint (SQLITE 787).
 */
class ObtenerOCrearProgresoUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uid: String): Progress {
        // Garantiza que el usuario existe en Room antes de
        // intentar insertar cualquier entidad relacionada
        asegurarUsuarioEnRoom(uid)
        return progresoRepository.getProgreso(uid) ?: crearProgresoInicial(uid)
    }

    /**
     * Si el usuario no está en Room (p.ej. primer arranque con sesión
     * de Firebase restaurada, o registro sin haber pasado por Login),
     * lo guarda ahora para que los FK constraints no fallen.
     */
    private suspend fun asegurarUsuarioEnRoom(uid: String) {
        if (userRepository.getUserById(uid) == null) {
            val userFromAuth = authRepository.getCurrentUser()
            val user = User(
                idUsuario    = uid,
                email        = userFromAuth?.email        ?: "",
                name         = userFromAuth?.name         ?: "Usuario",
                lastName     = userFromAuth?.lastName     ?: "Vita",
                currentLevel = 1,
                currentXp    = 0
            )
            userRepository.saveUser(user)
        }
    }

    private suspend fun crearProgresoInicial(uid: String): Progress {
        val nuevo = Progress(
            id         = 0,
            userId     = uid,
            level      = 1,
            xp         = 0,
            streakDays = 0,
            bmi        = 0f,
            weight     = 0f,
            date       = DateTimeUtils.getTodayMillis()
        )
        progresoRepository.insertarProgreso(nuevo)
        return nuevo
    }
}