package com.nutrino.audiocutter.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {
    @Singleton
    @Provides
    fun provideExoplayer(@ApplicationContext context: Context): ExoPlayer{
        return ExoPlayer.Builder(context).build()

    }



}