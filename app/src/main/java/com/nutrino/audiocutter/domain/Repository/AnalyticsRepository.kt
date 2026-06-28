package com.nutrino.audiocutter.domain.Repository

import android.os.Bundle

interface AnalyticsRepository {
    suspend fun logEvents(eventName: String, params: Bundle?)
    suspend fun screenViewLog(screenName: String, bundle: Bundle)
    fun logEventsNonSuspend(eventName: String, params: Bundle?)
}
