package com.example.vita.di

import com.example.vita.data.local.dao.*
import com.example.vita.data.remote.firebase.FirebaseAuthDataSource
import com.example.vita.data.repository.*
import com.example.vita.domain.repository.*
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideAuthRepository(ds: FirebaseAuthDataSource): AuthRepository =
        AuthRepositoryImpl(ds)

    @Provides @Singleton
    fun provideChallengeRepository(dao: ChallengeDao): ChallengeRepository =
        ChallengeRepositoryImpl(dao)

    @Provides @Singleton
    fun provideChatRepository(
        @ChatBotApi generativeModel: GenerativeModel,
        chatMessageDao: ChatMessageDao,
        authRepository: AuthRepository              // nuevo parámetro
    ): ChatRepository = ChatRepositoryImpl(generativeModel, chatMessageDao, authRepository)

    @Provides @Singleton
    fun provideUserRepository(dao: UserDao): UserRepository = UserRepositoryImpl(dao)

    @Provides @Singleton
    fun provideProfileRepository(dao: ProfileDao): ProfileRepository = ProfileRepositoryImpl(dao)

    @Provides @Singleton
    fun provideFoodRepository(fDao: FoodDao, pDao: FoodPreferenceDao): FoodRepository =
        FoodRepositoryImpl(fDao, pDao)

    @Provides @Singleton
    fun provideGamesRepository(gameDao: GameDao, userDao: UserDao): GameRepository =
        GameRepositoryImpl(gameDao, userDao)

    @Provides @Singleton
    fun provideMealRepository(dao: MealDao): MealRepository = MealRepositoryImpl(dao)

    @Provides @Singleton
    fun provideProgresoRepository(dao: ProgressDao): ProgresoRepository =
        ProgresoRepositoryImpl(dao)

    @Provides @Singleton
    fun provideAchievementRepository(dao: ArchivementDao): AchievementRepository =
        AchievementRepositoryImpl(dao)
}