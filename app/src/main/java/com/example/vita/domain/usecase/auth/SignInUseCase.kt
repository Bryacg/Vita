package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User? {
        return authRepository.signInWithCredentials(email, password)
    }
}
