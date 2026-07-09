package com.nutrino.audiocutter.di.module

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import com.nutrino.audiocutter.data.RepoImpl.AnalyticsRepositoryImpl
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import com.nutrino.audiocutter.domain.UseCases.LogEventUseCase
import com.nutrino.audiocutter.domain.UseCases.ScreenViewLogUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseAnalytics {
    @Provides
    @Singleton
    fun provideAnalytics(): FirebaseAnalytics{
        return Firebase.analytics
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        firebaseAnalytics: FirebaseAnalytics,
        crashAnalyticsHelper: CrashAnalyticsHelper
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            firebaseAnalytics = firebaseAnalytics,
            crashAnalyticsHelper = crashAnalyticsHelper
        )
    }

    @Provides
    fun provideLogEventUseCase(repository: AnalyticsRepository): LogEventUseCase {
        return LogEventUseCase(repository)
    }

    @Provides
    fun provideScreenViewLogUseCase(repository: AnalyticsRepository): ScreenViewLogUseCase {
        return ScreenViewLogUseCase(repository)
    }
}
