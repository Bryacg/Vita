package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 1. REACIVIDAD: Al devolver Flow, Room avisará a la CardInf cada vez que cambie el XP
    @Query("SELECT * FROM users WHERE idUsuario = :uid LIMIT 1")
    fun getUserStream(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE idUsuario = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    // 2. SINCRONIZACIÓN: Cambiamos el nombre para que coincida con UserRepositoryImpl
    @Query("UPDATE users SET currentXp = :xp, currentLevel = :level WHERE idUsuario = :uid")
    suspend fun updateUserXpAndLevel(uid: String, xp: Int, level: Int)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // 3. LIMPIEZA: Útil para cerrar sesión
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}