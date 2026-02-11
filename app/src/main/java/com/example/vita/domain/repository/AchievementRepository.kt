package com.example.vita.domain.repository

import com.example.vita.domain.model.Achievement

interface AchievementRepository {
        suspend fun saveAchievement(achievement: Achievement)
        suspend fun getAchievementsByUser(uid: String): List<Achievement>
        suspend fun unlockAchievement(id: Long, uid: String)
        suspend fun deleteAchievement(achievement: Achievement)

}