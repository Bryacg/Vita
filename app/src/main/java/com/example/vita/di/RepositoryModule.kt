package com.example.vita.di

import com.example.vita.data.local.dao.ArchivementDao
import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.local.dao.ChatMessageDao
import com.example.vita.data.local.dao.FoodDao
import com.example.vita.data.local.dao.FoodPreferenceDao
import com.example.vita.data.local.dao.GameDao
import com.example.vita.data.local.dao.MealDao
import com.example.vita.data.local.dao.ProfileDao
import com.example.vita.data.local.dao.ProgressDao
import com.example.vita.data.local.dao.UserDao
import com.example.vita.data.local.datasource.GodotGameDataSource
import com.example.vita.data.remote.firebase.FirebaseAuthDataSource
import com.example.vita.data.repository.AchievementRepositoryImpl
import com.example.vita.data.repository.AuthRepositoryImpl
import com.example.vita.data.repository.ChallengeRepositoryImpl
import com.example.vita.data.repository.ChatRepositoryImpl
import com.example.vita.data.repository.FoodRepositoryImpl
import com.example.vita.data.repository.GameRepositoryImpl
import com.example.vita.data.repository.GodotGameRepositoryImpl
import com.example.vita.data.repository.MealRepositoryImpl
import com.example.vita.data.repository.ProfileRepositoryImpl
import com.example.vita.data.repository.ProgresoRepositoryImpl
import com.example.vita.data.repository.UserRepositoryImpl
import com.example.vita.domain.repository.AchievementRepository
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ChallengeRepository
import com.example.vita.domain.repository.ChatRepository
import com.example.vita.domain.repository.FoodRepository
import com.example.vita.domain.repository.GameRepository
import com.example.vita.domain.repository.GodotGameRepository
import com.example.vita.domain.repository.MealRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
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

    @Provides @Singleton
    fun provideGodotGameDataSource(): GodotGameDataSource =
        GodotGameDataSource()

    @Provides @Singleton
    fun provideGodotGameRepository(dataSource: GodotGameDataSource): GodotGameRepository =
        GodotGameRepositoryImpl(dataSource)
}