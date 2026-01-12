package com.example.vita.data.local.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_result",
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
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val name: String,      // Nombre del evento
    val weight: Int,      // Peso o importancia del evento.
    val xpEarned: Int,    // Experiencia ganada por el evento.
    val date: Long       // Fecha del evento.
)
