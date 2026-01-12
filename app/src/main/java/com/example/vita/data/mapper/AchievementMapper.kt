package com.example.vita.data.mapper

import com.example.vita.data.local.entities.AchievementEntity
import com.example.vita.domain.model.Achievement

fun AchievementEntity.toDomain() = Achievement(
    id = id,
    userId = userId,
    name = name,
    description = description,
    unlocked = unlocked
)

fun Achievement.toEntity() = AchievementEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    unlocked = unlocked
)
