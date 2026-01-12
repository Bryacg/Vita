package com.example.vita.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vita.data.local.entities.FoodEntity



@Dao
interface FoodDao {
    // Inserta o reemplaza el commida usuario.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)


    // Obtiene las comidas usuario específico por id.
    @Query("SELECT * FROM food WHERE id = :uid LIMIT 1")
    suspend fun getFood(uid: Long): FoodEntity?
}