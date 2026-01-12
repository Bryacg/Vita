package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.AchievementEntity

@Dao
interface ArchivementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievement WHERE userId = :uid")
    suspend fun getAchievementsByUser(uid: String): List<AchievementEntity>

    @Query("UPDATE achievement SET unlocked = 1 WHERE id = :id AND userId = :uid")
    suspend fun unlockAchievement(id: Long, uid: String)

    @Delete
    suspend fun deleteAchievement(achievement: AchievementEntity)
}
