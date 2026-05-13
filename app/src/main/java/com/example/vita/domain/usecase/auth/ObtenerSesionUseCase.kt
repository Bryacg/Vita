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
    data class ResultadoSesion(
        val necesitaPerfil: Boolean
    )

    suspend operator fun invoke(): ResultadoSesion {
        val uid = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("No hay usuario autenticado")

        val userFromAuth = authRepository.getCurrentUser()
        val existingUser = userRepository.getUserById(uid)

        if (existingUser == null) {
            val newUser = User(
                idUsuario    = uid,
                email        = userFromAuth?.email ?: "",
                name         = userFromAuth?.name ?: "Usuario",
                lastName     = userFromAuth?.lastName ?: "Vita",
                currentLevel = 1,
                currentXp    = 0
            )
            userRepository.saveUser(newUser)
        }

        val profile = profileRepository.getProfileByUserId(uid)
        return ResultadoSesion(
            necesitaPerfil = profile == null || profile.weight == 0f
        )
    }
}