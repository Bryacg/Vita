package com.example.vita.data.local.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal",
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
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val name: String,    // Nombre de la comida ingerida.
    val calories: Int,  // Cantidad de calorías estimadas.
    val healthyScore: Int, // 0–100  // Puntaje de calidad nutricional (0–100).
    val date: Long         // timestamp // Fecha y hora del consumo en formato timestamp
)
