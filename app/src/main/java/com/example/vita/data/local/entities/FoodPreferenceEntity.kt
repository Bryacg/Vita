package com.example.vita.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_preference",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("foodId")]
)
data class FoodPreferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,   // Usuario dueño de la preferencia.
    val foodId: Long,     // Alimento al que se asocia la preferencia.
    val preferenceType: String // LIKE, DISLIKE, ALLERGY
)
