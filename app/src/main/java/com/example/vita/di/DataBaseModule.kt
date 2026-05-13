package com.example.vita.di

import android.content.Context
import androidx.room.Room
import com.example.vita.data.local.db.AppDatabase
import com.example.vita.data.local.db.MIGRATION_1_2
import com.example.vita.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "vita_database"
        )
            .addMigrations(MIGRATION_1_2)   // migración segura, sin perder datos
            .build()
    }

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
    @Provides fun provideFoodDao(db: AppDatabase): FoodDao = db.foodDao()
    @Provides fun provideFoodPreferenceDao(db: AppDatabase): FoodPreferenceDao = db.foodPreferenceDao()
    @Provides fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()
    @Provides fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()
    @Provides fun provideArchivementDao(db: AppDatabase): ArchivementDao = db.archivementDao()
    @Provides fun provideChallengeDao(db: AppDatabase): ChallengeDao = db.challengeDao()
    @Provides fun provideGameResultDao(db: AppDatabase): GameDao = db.gameResultDao()
    @Provides fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()
}