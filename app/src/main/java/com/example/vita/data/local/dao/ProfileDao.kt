package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.ProfileEntity

@Dao
interface ProfileDao {
    // Inserta o reemplaza el perfil biométrico del usuario.
    // Solo puede existir uno por usuario (relación 1:1).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    // Obtiene el perfil biométrico de un usuario específico.
    @Query("SELECT * FROM profile WHERE userId = :uid LIMIT 1")
    suspend fun getProfileByUser(uid: String): ProfileEntity?

    @Query("DELETE FROM profile WHERE userId = :userId")
    suspend fun deleteProfileByUserId(userId: String)
}
