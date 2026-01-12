package com.example.vita.data.mapper


import com.example.vita.data.local.entities.FoodEntity

import com.example.vita.domain.model.Food


fun FoodEntity.toDomain() = Food(
    id=id,
    name=name,
    category=category,
)

fun Food.toEntity() = FoodEntity(
    id=id,
    name=name,
    category=category,
)