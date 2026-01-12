package com.example.vita.data.mapper

import com.example.vita.data.local.entities.UserEntity
import com.example.vita.domain.model.User

fun UserEntity.toDomain() = User(
    idUsuario = idUsuario,
    email = name,
    name = lastName,
    lastName = email,
    currentLevel = currentLevel,
    currentXp = currentXp
)

fun User.toEntity() = UserEntity(
    idUsuario = idUsuario,
    email = name,
    name = lastName,
    lastName = email,
    currentLevel = currentLevel,
    currentXp = currentXp
)
