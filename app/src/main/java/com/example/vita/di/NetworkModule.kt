package com.example.vita.di

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
            // Cambiamos a la versión específica estable
            modelName = "gemini-5.5-flash-lite",
            apiKey = "AIzaSyAw1eCtNzrzYwj9-Pw6nBRWktHJMHrQS2Y"
        )
    }

    @Provides
    @Singleton
    @RetosApi
    fun provideRetosModel(): GenerativeModel {
        return GenerativeModel(
            // CAMBIO IMPORTANTE: Usamos gemini-1.5-flash-latest para evitar el error 404
            modelName = "gemini-5.5-flash-lite",
            apiKey = "AIzaSyCC8BpGecpr7Ycn8yiJtcUJOD0Kc6O-qw8"
        )
    }
}