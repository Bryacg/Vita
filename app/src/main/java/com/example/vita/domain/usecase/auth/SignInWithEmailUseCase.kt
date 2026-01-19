package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Aquí podrías agregar validaciones de negocio antes de llamar al repositorio
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Campos vacíos"))
        }
        return repository.login(email, password)
    }
}