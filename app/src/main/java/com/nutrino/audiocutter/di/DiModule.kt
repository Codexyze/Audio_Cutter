package com.nutrino.audiocutter.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.nutrino.audiocutter.data.RepoImpl.AudioTimmerRepoImpl
import com.nutrino.audiocutter.domain.Repository.AudioTrimmerRepository
import com.nutrino.audiocutter.domain.UseCases.TrimAudioUseCase
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

    @UnstableApi
    @Provides
    fun provideAudioTrimmerRepositoryObj(@ApplicationContext context: Context): AudioTrimmerRepository {
        return AudioTimmerRepoImpl()
    }

    @Provides
    fun provideAudioTrimmerUseCaseObj(repository: AudioTrimmerRepository): TrimAudioUseCase {
        return TrimAudioUseCase(repository = repository)

    }



}