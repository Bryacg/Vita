package com.example.vita.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val idUsuario: String,          // Email del usuario autenticado en Firebase. Se usa para identificación visual y recuperación de cuenta.
    val email: String,             // Se usa para identificación visual y recuperación de cuenta.
    val name: String,              // Nombre del usuario.
    val lastName: String,          // Apellido del usuario
    val currentLevel: Int = 1,     // Nivel actual del jugador dentro del sistema gamificado
    val currentXp: Int = 0         // Experiencia actual acumulada por el usuario.
)
