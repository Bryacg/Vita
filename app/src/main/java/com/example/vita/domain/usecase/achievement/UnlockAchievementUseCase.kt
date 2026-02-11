package com.example.vita.domain.usecase.achievement

import com.example.vita.domain.model.Achievement
import com.example.vita.domain.model.AchievementType
import com.example.vita.domain.repository.AchievementRepository
import javax.inject.Inject

class UnlockAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {
    suspend operator fun invoke(userId: String, tipo: AchievementType) {
        val achievement = Achievement(
            id = tipo.id,
            userId = userId,
            name = tipo.title,
            description = tipo.desc,
            unlocked = true
        )
        // Solo guardamos si no existe ya como desbloqueado
        achievementRepository.saveAchievement(achievement)
    }
}