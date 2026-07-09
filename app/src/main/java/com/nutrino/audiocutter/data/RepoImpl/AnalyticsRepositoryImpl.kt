package com.nutrino.audiocutter.data.RepoImpl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.nutrino.audiocutter.core.crashanalytics.CrashAnalyticsHelper
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val crashAnalyticsHelper: CrashAnalyticsHelper
): AnalyticsRepository{
    override suspend fun logEvents(eventName: String, params: Bundle?) {
        try {
            firebaseAnalytics.logEvent(
                eventName , params
            )
            crashAnalyticsHelper.successLog("Analytics", "Logged event: $eventName")
        }catch (e: Exception){
            crashAnalyticsHelper.logNonFatalException(e, "Error logging event: $eventName")
            crashAnalyticsHelper.errorLog("Analytics", "Failed to log event: $eventName")
        }

    }

    override suspend fun screenViewLog(screenName: String , bundle: Bundle) {
        try {
            val data = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, data)
            crashAnalyticsHelper.setCustomKey("last_screen", screenName)
            crashAnalyticsHelper.successLog("Analytics", "Screen View: $screenName")
        }catch (e: Exception){
            crashAnalyticsHelper.logNonFatalException(e, "Error logging screen view: $screenName")
            crashAnalyticsHelper.errorLog("Analytics", "Failed to log screen view: $screenName")
        }

    }

    override fun logEventsNonSuspend(eventName: String, params: Bundle?) {
        try {
            firebaseAnalytics.logEvent(eventName, params)
            crashAnalyticsHelper.successLog("Analytics", "Logged event (non-suspend): $eventName")
        } catch (e: Exception) {
            crashAnalyticsHelper.logNonFatalException(e, "Error logging event (non-suspend): $eventName")
            crashAnalyticsHelper.errorLog("Analytics", "Failed to log event (non-suspend): $eventName")
        }
    }

}
