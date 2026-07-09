package com.nutrino.audiocutter.di.module

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import com.nutrino.audiocutter.core.media.MediaPlayerManager
import com.nutrino.audiocutter.data.RepoImpl.AdsRepositoryImpl
import com.nutrino.audiocutter.data.RepoImpl.AudioSpeedRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.AudioTimmerRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.ConvertAudioFormatRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.GetAllSongsRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.MultiCropAudioRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.MultiCropVideoRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.RecordAudioRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.AudioVolumeBoosterRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.MuteVideoRepoImpl
import com.nutrino.audiocutter.data.RepoImpl.VideoRepImpl
import com.nutrino.audiocutter.data.RepoImpl.VideoSpeedRepoImpl
import com.nutrino.audiocutter.domain.Repository.AdsRepository
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import com.nutrino.audiocutter.domain.Repository.AudioSpeedRepository
import com.nutrino.audiocutter.domain.Repository.AudioTrimmerRepository
import com.nutrino.audiocutter.domain.Repository.ConvertAudioFormatRepository
import com.nutrino.audiocutter.domain.Repository.GetAllSongRepository
import com.nutrino.audiocutter.domain.Repository.MultiCropAudioRepository
import com.nutrino.audiocutter.domain.Repository.MultiCropVideoRepository
import com.nutrino.audiocutter.domain.Repository.MuteVideoRepository
import com.nutrino.audiocutter.domain.Repository.RecordAudioRepository
import com.nutrino.audiocutter.domain.Repository.AudioVolumeBoosterRepository
import com.nutrino.audiocutter.domain.Repository.VideoRepository
import com.nutrino.audiocutter.domain.Repository.VideoSpeedRepository
import com.nutrino.audiocutter.domain.UseCases.ChangeAudioSpeedUseCase
import com.nutrino.audiocutter.domain.UseCases.ChangeVideoSpeedUseCase
import com.nutrino.audiocutter.domain.UseCases.ConvertAudioFormatUseCase
import com.nutrino.audiocutter.domain.UseCases.GetAllSongsForMergeUseCase
import com.nutrino.audiocutter.domain.UseCases.GetAllVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.LoadAdUseCase
import com.nutrino.audiocutter.domain.UseCases.MergeSongsUseCase
import com.nutrino.audiocutter.domain.UseCases.MultiCropAudioUseCase
import com.nutrino.audiocutter.domain.UseCases.MultiCropVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.MuteVideoUseCase
import com.nutrino.audiocutter.domain.UseCases.RecordAudioUseCase
import com.nutrino.audiocutter.domain.UseCases.BoostAudioVolumeUseCase
import com.nutrino.audiocutter.domain.UseCases.ShowAdUseCase
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
    @Provides
    fun provideExoplayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()

    }

    @UnstableApi
    @Provides
    fun provideAudioTrimmerRepositoryObj(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): AudioTrimmerRepository {
        return AudioTimmerRepoImpl(
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideAudioTrimmerUseCaseObj(repository: AudioTrimmerRepository): TrimAudioUseCase {
        return TrimAudioUseCase(repository = repository)

    }

    @UnstableApi
    @Provides
    fun provideGetAllSongUseCaseObj(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): GetAllSongRepository {
        return GetAllSongsRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideMediaPlayerManager(exoPlayer: ExoPlayer): MediaPlayerManager {
        return MediaPlayerManager(exoPlayer = exoPlayer)
    }

    @UnstableApi
    @Provides
    fun provideVideoRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): VideoRepository {
        return VideoRepImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @UnstableApi
    @Provides
    fun provideVideoSpeedRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): VideoSpeedRepository {
        return VideoSpeedRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideChangeVideoSpeedUseCase(repository: VideoSpeedRepository): ChangeVideoSpeedUseCase {
        return ChangeVideoSpeedUseCase(repository = repository)
    }

    @Provides
    fun provideGetAllVideoUseCase(videoRepository: VideoRepository): GetAllVideoUseCase {
        return GetAllVideoUseCase(repository = videoRepository)
    }

    @Provides
    fun provideTrimVideoUseCase(videoRepository: VideoRepository): TrimVideoUseCase {
        return TrimVideoUseCase(repository = videoRepository)
    }

    @Provides
    fun provideGetAllSongsForMergeUseCase(repository: GetAllSongRepository): GetAllSongsForMergeUseCase {
        return GetAllSongsForMergeUseCase(getAllSongRepository = repository)
    }

    @Provides
    fun provideMergeSongsUseCase(repository: GetAllSongRepository): MergeSongsUseCase {
        return MergeSongsUseCase(repository = repository)
    }

    @Provides
    @Singleton
    fun provideAdsRepository(
        @ApplicationContext context: Context,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): AdsRepository {
        return AdsRepositoryImpl(context = context, crashAnalyticsHelper = crashAnalyticsHelper)
    }

    @Provides
    fun provideLoadAdUseCase(repository: AdsRepository): LoadAdUseCase {
        return LoadAdUseCase(repository = repository)
    }

    @Provides
    fun provideShowAdUseCase(repository: AdsRepository): ShowAdUseCase {
        return ShowAdUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideMultiCropRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): MultiCropAudioRepository {
        return MultiCropAudioRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @UnstableApi
    @Provides
    fun provideMultiCropAudioUseCase(repository: MultiCropAudioRepository): MultiCropAudioUseCase {
        return MultiCropAudioUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideMultiCropVideoRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): MultiCropVideoRepository {
        return MultiCropVideoRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @UnstableApi
    @Provides
    fun provideMultiCropVideoUseCase(repository: MultiCropVideoRepository): MultiCropVideoUseCase {
        return MultiCropVideoUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideConvertAudioFormatRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): ConvertAudioFormatRepository {
        return ConvertAudioFormatRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @UnstableApi
    @Provides
    fun provideConvertAudioFormatUseCase(repository: ConvertAudioFormatRepository): ConvertAudioFormatUseCase {
        return ConvertAudioFormatUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    @Singleton
    fun provideRecordAudioRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): RecordAudioRepository {
        return RecordAudioRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideRecordAudioUseCase(repository: RecordAudioRepository): RecordAudioUseCase {
        return RecordAudioUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideAudioSpeedRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): AudioSpeedRepository {
        return AudioSpeedRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideChangeAudioSpeedUseCase(repository: AudioSpeedRepository): ChangeAudioSpeedUseCase {
        return ChangeAudioSpeedUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideMuteVideoRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): MuteVideoRepository {
        return MuteVideoRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideMuteVideoUseCase(repository: MuteVideoRepository): MuteVideoUseCase {
        return MuteVideoUseCase(repository = repository)
    }

    @UnstableApi
    @Provides
    fun provideAudioVolumeBoosterRepo(
        @ApplicationContext context: Context,
        analyticsRepository: AnalyticsRepository,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): AudioVolumeBoosterRepository {
        return AudioVolumeBoosterRepoImpl(
            context = context,
            analyticsRepository = analyticsRepository,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideBoostAudioVolumeUseCase(repository: AudioVolumeBoosterRepository): BoostAudioVolumeUseCase {
        return BoostAudioVolumeUseCase(repository = repository)
    }

}