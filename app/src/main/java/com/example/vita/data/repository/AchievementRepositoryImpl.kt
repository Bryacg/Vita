package com.example.vita.data.repository

import com.example.vita.data.local.dao.ArchivementDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Achievement
import com.example.vita.domain.repository.AchievementRepository
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: ArchivementDao
) : AchievementRepository {

    override suspend fun saveAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement.toEntity())
    }

    override suspend fun getAchievementsByUser(uid: String): List<Achievement> {
        return achievementDao.getAchievementsByUser(uid).map { it.toDomain() }
    }

    override suspend fun unlockAchievement(id: Long, uid: String) {
        achievementDao.unlockAchievement(id, uid)
    }

    override suspend fun deleteAchievement(achievement: Achievement) {
        achievementDao.deleteAchievement(achievement.toEntity())
    }
}