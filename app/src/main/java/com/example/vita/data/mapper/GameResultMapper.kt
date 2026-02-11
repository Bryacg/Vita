package com.example.vita.data.mapper

import com.example.vita.data.local.entities.GameEntity
import com.example.vita.domain.model.GameResult

fun GameResult.toEntity(): GameEntity {
    return GameEntity(
        id = this.id,
        userId = this.userId,
        name = this.name,
        weight = this.weight,
        xpEarned = this.xpEarned,
        date = this.date
        // ... otros campos
    )
}

fun GameEntity.toDomain(): GameResult {
    return GameResult(
        id = this.id,
        userId = this.userId,
        name = this.name,
        weight = this.weight, // Valor por defecto
        xpEarned = this.xpEarned,
        date = this.date
    )
}