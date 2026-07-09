package com.nutrino.audiocutter.di.module

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CrashAnalytics {

    @Singleton
    @Provides
    fun getCrashAnalytics(): FirebaseCrashlytics{
        return Firebase.crashlytics
    }

    @Singleton
    @Provides
    fun getCrashAnalyticsHelper(crashAnalytics: FirebaseCrashlytics): CrashAnalyticsHelper {
        return CrashAnalyticsHelper(crashAnalytics)
    }
}