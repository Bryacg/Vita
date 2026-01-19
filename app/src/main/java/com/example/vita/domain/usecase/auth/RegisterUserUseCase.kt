package com.example.vita.domain.usecase.auth

import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Definimos el invoke para aceptar email y password y retornar Result
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Creamos un objeto User temporal para el repositorio
        val tempUser = User(idUsuario = "", email = email, name = "", lastName = "", currentLevel = 1, currentXp = 0)
        return repository.register(tempUser, password)
    }
}