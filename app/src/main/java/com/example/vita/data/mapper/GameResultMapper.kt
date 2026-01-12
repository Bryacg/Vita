package com.example.vita.data.mapper

import com.example.vita.data.local.entities.GameResultEntity
import com.example.vita.domain.model.GameResult
import kotlin.String

fun GameResultEntity.toDomain() = GameResult(
    id=id ,
    userId= userId,
    name= name,      // Nombre del evento
    weight= weight,      // Peso o importancia del evento.
    xpEarned= xpEarned,    // Experiencia ganada por el evento.
    date= date
)

fun GameResult.toEntity() = GameResultEntity(
    id=id ,
    userId= userId,
    name= name,      // Nombre del evento
    weight= weight,      // Peso o importancia del evento.
    xpEarned= xpEarned,    // Experiencia ganada por el evento.
    date= date
)