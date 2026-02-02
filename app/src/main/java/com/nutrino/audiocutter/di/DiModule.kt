package com.nutrino.audiocutter.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.nutrino.audiocutter.core.MediaPlayerManager
import com.nutrino.audiocutter.data.RepoImpl.AudioTimmerRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.GetAllSongsRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.VideoRepImpl
import com.nutrino.audiocutter.domain.Repository.AudioTrimmerRepository
import com.nutrino.audiocutter.domain.Repository.GetAllSongRepository
import com.nutrino.audiocutter.domain.Repository.VideoRepository
import com.nutrino.audiocutter.domain.UseCases.GetAllVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.TrimAudioUseCase
import com.nutrino.audiocutter.domain.UseCases.TrimVideoUseCase
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

    @Provides
    fun provideGetAllSongUseCaseObj(@ApplicationContext context: Context): GetAllSongRepository {
        return GetAllSongsRepoImpl(context = context)

    }

    @Provides
    fun provideMediaPlayerManager(exoPlayer: ExoPlayer): MediaPlayerManager {
        return MediaPlayerManager(exoPlayer =exoPlayer )
    }

    @UnstableApi
    @Provides
    fun provideVideoRepo(@ApplicationContext context: Context): VideoRepository{
        return VideoRepImpl(context = context)
    }

    @Provides
    fun provideGetAllVideoUseCase(videoRepository: VideoRepository): GetAllVideoUseCase{
        return GetAllVideoUseCase(repository =videoRepository )
    }

    @Provides
    fun provideTrimVideoUseCase(videoRepository: VideoRepository): TrimVideoUseCase{
        return TrimVideoUseCase(repository =videoRepository )
    }



}

