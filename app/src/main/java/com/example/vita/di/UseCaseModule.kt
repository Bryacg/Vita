package com.example.vita.di

import com.example.vita.domain.repository.*
import com.example.vita.domain.usecase.auth.*
import com.example.vita.domain.usecase.progreso.*
import com.example.vita.domain.usecase.retos.*
import com.example.vita.domain.usecase.juegos.*
import com.example.vita.domain.usecase.comida.*
import com.example.vita.domain.usecase.chat.*
import com.example.vita.domain.usecase.progreso.TrackRachaUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideSignInWithGoogleUseCase(repo: AuthRepository) = SignInWithGoogleUseCase(repo)
    @Provides fun provideSignInUseCase(repo: AuthRepository) = SignInUseCase(repo)
    @Provides fun provideRegisterUseCase(repo: AuthRepository) = RegisterUseCase(repo)
    @Provides fun provideSignOutUseCase(repo: AuthRepository) = SignOutUseCase(repo)

    @Provides fun provideAgregarXpUseCase(repo: ProgresoRepository) = AgregarXpUseCase(repo)
    @Provides fun provideActualizarNivelUseCase(repo: ProgresoRepository) = ActualizarNivelUseCase(repo)
    @Provides fun provideActualizarBmiUseCase(repo: ProgresoRepository) = ActualizarBmiUseCase(repo)
    @Provides fun provideTrackRachaUseCase(repo: ProgresoRepository) = TrackRachaUseCase(repo)
    @Provides fun provideResetearRachaUseCase(repo: ProgresoRepository) = ResetearRachaUseCase(repo)

    @Provides fun provideInsertarRetoUseCase(repo: ChallengeRepository) = InsertarRetoUseCase(repo)
    @Provides fun provideObtenerRetosActivosUseCase(repo: ChallengeRepository) = ObtenerRetosActivosUseCase(repo)
    @Provides fun provideActualizarProgresoRetoUseCase(repo: ChallengeRepository) = ActualizarProgresoRetoUseCase(repo)

    @Provides fun provideProcesarResultadoJuegoUseCase(repo: GamesRepository) = ProcesarResultadoJuegoUseCase(repo)
    @Provides fun provideRegistrarComidaUseCase(repo: MealRepository) = RegistrarComidaUseCase(repo)
    @Provides fun provideEnviarMensajeChatUseCase(repo: ChatRepository) = EnviarMensajeChatUseCase(repo)
}
