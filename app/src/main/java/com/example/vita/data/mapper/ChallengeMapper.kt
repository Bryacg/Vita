package com.example.vita.data.mapper

import com.example.vita.data.local.entities.ChallengeEntity
import com.example.vita.domain.model.Challenger

fun ChallengeEntity.toDomain(): Challenger = Challenger(
    id = id,
    userId = userId,
    name = name,
    description = description,
    type = type,
    targetValue = targetValue,
    currentValue = currentValue,
    status = status,
    deadline = deadline,
    createdAt = createdAt
)

fun Challenger.toEntity() = ChallengeEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    type = type,
    targetValue = targetValue,
    currentValue = currentValue,
    status = status,
    deadline = deadline,
    createdAt = createdAt
)