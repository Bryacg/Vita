package com.example.vita.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vita.data.local.entities.FoodEntity

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity): Long // Devuelve el ID generado

    @Query("SELECT * FROM food WHERE id = :id LIMIT 1")
    suspend fun getFoodById(id: Long): FoodEntity?

    // Nueva: Para saber si el alimento ya existe antes de crearlo
    @Query("SELECT * FROM food WHERE name = :name LIMIT 1")
    suspend fun getFoodByName(name: String): FoodEntity?
}