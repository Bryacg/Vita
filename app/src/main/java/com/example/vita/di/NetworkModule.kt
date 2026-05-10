package com.example.vita.di

import com.example.vita.BuildConfig // Asegúrate de importar tu propio BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.tuendpoint.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // --- CONFIGURACIÓN DE GEMINI ---
    @Provides
    @Singleton
    @ChatBotApi
    fun provideChatModel(): GenerativeModel {
        return GenerativeModel(
            // Usamos las variables inyectadas desde local.properties
            modelName = BuildConfig.MODEL_NAME,
            apiKey = BuildConfig.API_CHAT
        )
    }

    @Provides
    @Singleton
    @RetosApi
    fun provideRetosModel(): GenerativeModel {
        return GenerativeModel(
            modelName = BuildConfig.MODEL_NAME,
            apiKey = BuildConfig.API_RETOS
        )
    }
}