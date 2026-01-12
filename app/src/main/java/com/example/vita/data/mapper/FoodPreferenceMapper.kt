package com.example.vita.data.mapper

import com.example.vita.data.local.entities.FoodPreferenceEntity
import com.example.vita.domain.model.FoodPreference

fun FoodPreferenceEntity.toDomain() = FoodPreference(
    id = id,
    userId = userId,
    foodId = foodId,
    preferenceType = preferenceType
)

fun FoodPreference.toEntity() = FoodPreferenceEntity(
    id = id,
    userId = userId,
    foodId = foodId,
    preferenceType = preferenceType
)