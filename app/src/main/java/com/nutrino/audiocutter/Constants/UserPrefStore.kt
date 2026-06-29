package com.nutrino.audiocutter.Constants

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPrefStore {
    const val userPrefrenceStoreName = "userPrefranceStore"

    const val THEME_SELECTION = "theme_selection"

    // NEW KEYS
    val USAGE_COUNT = intPreferencesKey("usage_count")
    val LAST_USAGE_DATE = stringPreferencesKey("last_usage_date")

}