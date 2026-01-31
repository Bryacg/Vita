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

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource   // ← Hilt lo inyecta automáticamente
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuthDataSource)
    }

    @Provides
    @Singleton
    fun provideChallengeRepository(dao: ChallengeDao): ChallengeRepository {
        return ChallengeRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        generativeModel: GenerativeModel // Hilt lo buscará en el AIModule
    ): ChatRepository {
        return ChatRepositoryImpl(generativeModel)
    }

    @Provides
    @Singleton
    fun provideGamesRepository(dao: GameResultDao): GamesRepository {
        return GamesRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideMealRepository(dao: MealDao): MealRepository {
        return MealRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideProgresoRepository(dao: ProgressDao): ProgresoRepository {
        return ProgresoRepositoryImpl(dao)
    }
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }
}
