package com.example.vita.domain.usecase.auth

import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()      // Cierra sesión en Firebase
        userRepository.deleteUserData() // Borra los datos del usuario de Room
    }
}