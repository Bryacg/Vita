package com.example.vita.data.mapper

import com.example.vita.data.local.entities.MealEntity
import com.example.vita.domain.model.Meal
fun MealEntity.toDomain() = Meal(
    id = id,
    userId = userId,
    name = name,
    calories = calories,
    healthyScore = healthyScore,
    date = date
)

fun Meal.toEntity() = MealEntity(
    id = id,
    userId = userId,
    name = name,
    calories = calories,
    healthyScore = healthyScore,
    date = date
)