package com.nutrino.audiocutter.data.userPref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.Constants.UserPrefStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPrefStore.userPrefrenceStoreName)

class UserPrefrenceStore @Inject constructor(private val context: Context) {
    companion object{
        val THEME_SELECTION = stringPreferencesKey(UserPrefStore.THEME_SELECTION)
    }

    val themeSelection: Flow<String> = context.dataStore.data.map {
        it[THEME_SELECTION] ?: Colors.ORANGETHEME
    }

    suspend fun updateThemeSelection(theme: String){
        context.dataStore.edit {
            it[THEME_SELECTION] = theme
        }
    }



}