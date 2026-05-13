package com.example.vita.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vita.data.local.dao.*
import com.example.vita.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        FoodPreferenceEntity::class,
        ProgressEntity::class,
        MealEntity::class,
        FoodEntity::class,
        AchievementEntity::class,
        ChallengeEntity::class,
        GameEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun foodDao(): FoodDao
    abstract fun foodPreferenceDao(): FoodPreferenceDao
    abstract fun progressDao(): ProgressDao
    abstract fun mealDao(): MealDao
    abstract fun archivementDao(): ArchivementDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun gameResultDao(): GameDao
    abstract fun chatMessageDao(): ChatMessageDao
}