package com.nutrino.audiocutter.data.RepoImpl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
): AnalyticsRepository{
    override suspend fun logEvents(eventName: String, params: Bundle?) {
        try {
            firebaseAnalytics.logEvent(
                eventName , params
            )
        }catch (e: Exception){

        }

    }

    override suspend fun screenViewLog(screenName: String , bundle: Bundle) {
        try {
            val data = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, data)
        }catch (e: Exception){

        }

    }

    override fun logEventsNonSuspend(eventName: String, params: Bundle?) {
        try {
            firebaseAnalytics.logEvent(eventName, params)
        } catch (e: Exception) {
            // No-op
        }
    }

}
