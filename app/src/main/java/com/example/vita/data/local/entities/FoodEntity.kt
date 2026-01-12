package com.example.vita.data.local.entities




import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class FoodEntity(
    // Identificador único del alimento.
    // Se autogenera porque no depende de Firebase
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // Nombre del alimento (ej. "Manzana")
    val category: String  // Categoría nutricional del alimento. Ej. proteína, carbohidrato, ultraprocesado.
)