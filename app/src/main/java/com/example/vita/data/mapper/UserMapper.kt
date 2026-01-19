package com.example.vita.data.mapper

import com.example.vita.data.local.entities.UserEntity
import com.example.vita.domain.model.User
import com.google.firebase.auth.FirebaseUser

// 1. Mapeo entre Entidad Local (Room) y Dominio
fun UserEntity.toDomain() = User(
        idUsuario = idUsuario,
        email = email,         // Corregido: email con email
        name = name,           // Corregido: name con name
        lastName = lastName,   // Corregido: lastName con lastName
        currentLevel = currentLevel,
        currentXp = currentXp
    )

fun User.toEntity() = UserEntity(
    idUsuario = idUsuario,
    email = email,
    name = name,
    lastName = lastName,
    currentLevel = currentLevel,
    currentXp = currentXp
)

// 2. Mapeo desde Firebase a Dominio (Para el DataSource)
fun FirebaseUser.toDomain(): User = User(
    idUsuario = uid,
    email = email ?: "",
    name = displayName ?: "Usuario Vita",
    lastName = "", // Firebase no suele dar el apellido por separado
    currentLevel = 1,
    currentXp = 0
)