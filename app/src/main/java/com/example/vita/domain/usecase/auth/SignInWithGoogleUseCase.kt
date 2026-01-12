package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): User? {
        return authRepository.signInWithGoogle(token)
    }
}
