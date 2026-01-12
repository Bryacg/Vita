package com.example.vita.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WorkMarkerModule {
    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager =
        WorkManager.getInstance(context)
}
