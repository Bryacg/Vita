package com.example.vita.di

import com.example.vita.data.local.dao.*
import com.example.vita.data.remote.firebase.FirebaseAuthDataSource
import com.example.vita.data.repository.*
import com.example.vita.domain.repository.*
import com.example.vita.domain.usecase.retos.GenerarRetosIAUseCase
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        ds: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(ds)

    @Provides
    @Singleton
    fun provideChallengeRepository(
        dao: ChallengeDao,
        generarRetosIAUseCase: GenerarRetosIAUseCase // <--- CAMBIA ESTO
    ): ChallengeRepository = ChallengeRepositoryImpl(dao, generarRetosIAUseCase)

    @Provides
    @Singleton
    fun provideChatRepository(
        @ChatBotApi generativeModel: GenerativeModel // Usa el motor de Chat
    ): ChatRepository = ChatRepositoryImpl(generativeModel)

    @Provides
    @Singleton
    fun provideUserRepository(dao: UserDao): UserRepository = UserRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideProfileRepository(dao: ProfileDao): ProfileRepository = ProfileRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideFoodRepository(
        fDao: FoodDao,
        pDao: FoodPreferenceDao
    ): FoodRepository = FoodRepositoryImpl(fDao, pDao)

    @Provides
    @Singleton
    fun provideGamesRepository(dao: GameResultDao): GamesRepository = GamesRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideMealRepository(dao: MealDao): MealRepository = MealRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideProgresoRepository(dao: ProgressDao): ProgresoRepository = ProgresoRepositoryImpl(dao)
}