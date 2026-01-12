package com.example.vita.domain.model

data class User(
    val idUsuario: String,          // Email del usuario autenticado en Firebase. Se usa para identificación visual y recuperación de cuenta.
    val email: String,             // Se usa para identificación visual y recuperación de cuenta.
    val name: String,              // Nombre del usuario.
    val lastName: String,          // Apellido del usuario
    val currentLevel: Int ,     // Nivel actual del jugador dentro del sistema gamificado
    val currentXp: Int ,
)
