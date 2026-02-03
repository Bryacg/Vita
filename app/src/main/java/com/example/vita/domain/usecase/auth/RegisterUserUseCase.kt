package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Cambiamos los parámetros para recibir el objeto User completo
    suspend operator fun invoke(user: User, password: String): Result<User> {
        // Ahora pasamos el usuario con su nombre y apellido reales al repositorio
        return repository.register(user, password)
    }
}