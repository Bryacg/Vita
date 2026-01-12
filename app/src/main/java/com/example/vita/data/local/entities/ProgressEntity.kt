package com.example.vita.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "progress",
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
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,  // Identificador del registro histórico
    val userId: String,  // Usuario al que pertenece el registro
    val level: Int,    // Nivel del usuario en ese momento.
    val xp: Int,   // Experiencia acumulada.
    val weight: Float,   // Peso corporal registrado.
    val bmi: Float,      // Índice de Masa Corporal calculado.
    val date: Long,      // Fecha del snapshot.
    val streakDays: Int = 0 // Racha de días consecutivos
)