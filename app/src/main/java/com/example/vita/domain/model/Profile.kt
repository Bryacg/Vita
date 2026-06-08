package com.example.vita.domain.model

data class Profile(
    val id: Long = 0,
    val userId: String,
    val height: Float,
    val weight: Float,
    val age: Int,
    val gender: String
)
