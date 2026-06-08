package com.example.vita.data.mapper

import com.example.vita.data.local.entities.GameEntity
import com.example.vita.domain.model.GameResult

fun GameResult.toEntity(): GameEntity = GameEntity(
    id = id,
    userId = userId,
    name = name,
    weight = weight,
    xpEarned = xpEarned,
    date = date
)

fun GameEntity.toDomain(): GameResult = GameResult(
    id = id,
    userId = userId,
    name = name,
    weight = weight,
    xpEarned = xpEarned,
    date = date
)