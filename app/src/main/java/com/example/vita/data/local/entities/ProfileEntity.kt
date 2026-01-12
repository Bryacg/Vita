package com.example.vita.data.local.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "profile",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,        // Identifica de forma única el perfil biométrico.
    val userId: String,    // Clave foránea que referencia al usuario dueño del perfil. Mantiene la relación 1:1 con UserEntity.
    val height: Float,   // cm   Se usa para calcular IMC y TMB
    val weight: Float,   // kg   Peso corporal del usuario en kilogramos.
    val age: Int,        // Afecta el metabolismo basal.
    val gender: String    //Sexo biológico del usuario
)

