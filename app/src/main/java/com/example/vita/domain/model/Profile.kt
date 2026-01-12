package com.example.vita.domain.model

data class Profile(
    val id: Long,        // Identifica de forma única el perfil biométrico.
    val userId: String,    // Clave foránea que referencia al usuario dueño del perfil. Mantiene la relación 1:1 con UserEntity.
    val height: Float,   // cm   Se usa para calcular IMC y TMB
    val weight: Float,   // kg   Peso corporal del usuario en kilogramos.
    val age: Int,        // Afecta el metabolismo basal.
    val gender: String

)
