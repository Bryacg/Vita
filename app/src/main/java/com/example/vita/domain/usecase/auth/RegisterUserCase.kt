package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, password: String): User? {
        return authRepository.isUserLoggedInFlow()
    }
}
