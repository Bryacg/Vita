package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.UserEntity

@Dao
interface UserDao {
    // Inserta un usuario en la base de datos.
    // Si el usuario ya existe (mismo idUsuario), lo reemplaza.
    // Esto es ideal para sincronización con Firebase.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Obtiene un usuario específico por su UID de Firebase.
    // LIMIT 1 evita lecturas innecesarias.
    @Query("SELECT * FROM users WHERE idUsuario = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?


    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    // Actualiza únicamente el nivel y la experiencia del usuario.
    // Se usa cuando se gana XP desde la gamificación.
    @Query("UPDATE users SET currentLevel = :level, currentXp = :xp WHERE idUsuario = :uid")
    suspend fun updateLevelXp(uid: String, level: Int, xp: Int)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}
