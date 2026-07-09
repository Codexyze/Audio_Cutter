package com.nutrino.audiocutter.core.crashanalytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class CrashAnalyticsHelper @Inject constructor(
    private val crashAnalytics: FirebaseCrashlytics
) {

    /**
     * Logs a breadcrumb message. These will be visible in the 'Logs' tab
     * in the Firebase console for any subsequent crash or non-fatal report.
     */
    fun log(message: String) {
        crashAnalytics.log(message)
    }

    /**
     * Records a non-fatal exception. These appear as issues in the Firebase console.
     * Useful for logging errors that are caught but shouldn't happen.
     */
    fun logNonFatalException(throwable: Throwable, message: String? = null) {
        message?.let { log("Non-Fatal: $it") }
        crashAnalytics.recordException(throwable)
    }

    /**
     * Logs an error event with a feature name and message.
     * Sets a custom key for the feature to track its last error state.
     */
    fun errorLog(featureName: String, errorMessage: String) {
        log("ERROR | $featureName: $errorMessage")
        setCustomKey(featureName, "Error: $errorMessage")
    }

    /**
     * Records a string-based error as a non-fatal issue in Firebase.
     * Use this when you don't have a Throwable but want the error to 
     * show up in the Firebase "Issues" dashboard.
     */
    fun recordGeneralError(message: String) {
        log("General Error: $message")
        crashAnalytics.recordException(Exception(message))
    }

    /**
     * Sets a custom key-value pair to the crash report.
     * Use this for app state (e.g., current screen name or user preferences).
     */
    fun setCustomKey(key: String, value: String) {
        crashAnalytics.setCustomKey(key, value)
    }

    /**
     * Logs a success event with a feature name and message.
     * Sets a custom key for the feature to track its last successful state.
     */
    fun successLog(featureName: String, message: String) {
        log("SUCCESS | $featureName: $message")
        setCustomKey(featureName, "Success: $message")
    }

    fun setUserId(userId: String) {
        crashAnalytics.setUserId(userId)
    }
    
    
}
