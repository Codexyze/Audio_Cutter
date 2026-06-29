package com.nutrino.audiocutter.data.userPref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.Constants.UserPrefStore
import com.nutrino.audiocutter.domain.UseCases.revenueCat.IsUserProUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPrefStore.userPrefrenceStoreName)

class UserPrefrenceStore @Inject constructor(private val context: Context
    ) {
    companion object{
        val THEME_SELECTION = stringPreferencesKey(UserPrefStore.THEME_SELECTION)
    }

    val themeSelection: Flow<String> = context.dataStore.data.map {
        it[THEME_SELECTION] ?: Colors.ORANGETHEME
    }
    val usageCount: Flow<Int> = context.dataStore.data.map {
        it[UserPrefStore.USAGE_COUNT] ?: 0
    }
    val lastUsageDate: Flow<String> = context.dataStore.data.map {
        it[UserPrefStore.LAST_USAGE_DATE] ?: ""
    }

    suspend fun updateThemeSelection(theme: String){
        context.dataStore.edit {
            it[THEME_SELECTION] = theme
        }
    }



    suspend fun updateUsage(count: Int, date: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPrefStore.USAGE_COUNT] = count
            preferences[UserPrefStore.LAST_USAGE_DATE] = date
        }
    }



}