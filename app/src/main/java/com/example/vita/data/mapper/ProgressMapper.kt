package com.example.vita.data.mapper

import com.example.vita.data.local.entities.ProgressEntity
import com.example.vita.domain.model.Progress

fun ProgressEntity.toDomain() = Progress(
    id = id,
    userId = userId,
    level = level,
    xp = xp,
    weight = weight,
    bmi=bmi,
    date=date,
    streakDays=streakDays
)

fun Progress.toEntity() = ProgressEntity(
    id = id,
    userId = userId,
    level = level,
    xp = xp,
    weight = weight,
    bmi=bmi,
    date=date,
    streakDays=streakDays
)