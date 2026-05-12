package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Encapsula getUserById para que los ViewModels no inyecten
 * UserRepository directamente.
 */
class ObtenerUsuarioUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String): User? {
        return userRepository.getUserById(uid)
    }
}