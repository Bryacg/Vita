package com.example.vita.di

import android.content.Context
import androidx.room.Room
import com.example.vita.data.local.db.AppDatabase
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
    fun provideDatabase(
        @ApplicationContext context: Context // <--- ESTA ANOTACIÓN ES OBLIGATORIA
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "vita_database"
        )
            .fallbackToDestructiveMigration() // útil en desarrollo
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideFoodDao(db: AppDatabase): FoodDao = db.FoodDao()

    @Provides
    fun provideFoodPreferenceDao(db: AppDatabase): FoodPreferenceDao = db.foodPreferenceDao()

    @Provides
    fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideArchivementDao(db: AppDatabase): ArchivementDao = db.archivementDao()

    @Provides
    fun provideChallengeDao(db: AppDatabase): ChallengeDao = db.challengeDao()

    @Provides
    fun provideGameResultDao(db: AppDatabase): GameResultDao = db.gameResultDao()
}
