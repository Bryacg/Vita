package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class ObtenerSesionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {
    data class ResultadoSesion(val necesitaPerfil: Boolean)

    /**
     * Para Login (email o Google): lee el nombre desde Firebase.
     */
    suspend operator fun invoke(): ResultadoSesion = invoke(usuarioRegistrado = null)

    /**
     * Para Registro con email: recibe el User con el nombre real del formulario
     * y lo guarda directamente en Room, sin depender de Firebase displayName.
     *
     * @param usuarioRegistrado User con los datos del formulario de registro.
     *   Si es null, lee los datos de Firebase (comportamiento de login normal).
     */
    suspend operator fun invoke(usuarioRegistrado: User?): ResultadoSesion {
        val uid = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("No hay usuario autenticado")

        val existingUser = userRepository.getUserById(uid)

        if (existingUser == null) {
            val newUser = if (usuarioRegistrado != null) {
                // ✅ Registro: usa los datos reales del formulario
                usuarioRegistrado.copy(idUsuario = uid)
            } else {
                // Login normal: lee de Firebase (Google ya tiene displayName)
                val userFromAuth = authRepository.getCurrentUser()
                User(
                    idUsuario    = uid,
                    email        = userFromAuth?.email ?: "",
                    name         = userFromAuth?.name ?: "Usuario",
                    lastName     = userFromAuth?.lastName ?: "Vita",
                    currentLevel = 1,
                    currentXp    = 0
                )
            }
            userRepository.saveUser(newUser)
        }

        val profile = profileRepository.getProfileByUserId(uid)
        return ResultadoSesion(
            necesitaPerfil = profile == null || profile.weight == 0f
        )
    }
}