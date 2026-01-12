package com.example.vita.data.local.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenge",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val name: String,     // Nombre del desafío
    val description: String, // Descripción del objetivo
    val type: String,  // Tipo de desafío (semanal, diario).
    val targetValue: Int,    // Valor objetivo que debe alcanzarse
    val currentValue: Int = 0,  // Progreso actual del desafío.
    val status: String, // Estado del desafío: ACTIVE, COMPLETED, EXPIRED
    val deadline: Long    // Fecha límite del desafío.
)
