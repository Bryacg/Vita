package com.example.vita.domain.model

/**
 * Logros o recompensas desbloqueadas por el usuario.
 */
data class Achievement(
    val id: Long,
    val userId: String,
    val name: String,
    val description: String,
    val unlocked: Boolean
)
