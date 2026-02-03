package com.example.vita.data.mapper

import com.example.vita.data.local.entities.ProfileEntity
import com.example.vita.domain.model.Profile

fun ProfileEntity.toDomain() = Profile(
    id=id,
    userId = userId,
    height = height,
    weight = weight,
    age = age,
    gender = gender,
)

fun Profile.toEntity() = ProfileEntity(
    id=id,
    userId = userId,
    height = height,
    weight = weight,
    age = age,
    gender = gender,
)