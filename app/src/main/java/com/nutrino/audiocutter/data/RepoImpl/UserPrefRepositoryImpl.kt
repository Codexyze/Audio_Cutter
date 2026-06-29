package com.nutrino.audiocutter.data.RepoImpl

import com.nutrino.audiocutter.data.userPref.UserPrefrenceStore
import com.nutrino.audiocutter.domain.Repository.UserPrefRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPrefRepositoryImpl @Inject constructor(
    private val userPrefrenceStore: UserPrefrenceStore
) : UserPrefRepository {

    override fun getThemeSelection(): Flow<String> {
        return userPrefrenceStore.themeSelection
    }

    override suspend fun updateThemeSelection(theme: String) {
        userPrefrenceStore.updateThemeSelection(theme = theme)
    }

    override fun getUsageCount(): Flow<Int> {
        return userPrefrenceStore.usageCount
    }

    override fun getLastUsageDate(): Flow<String> {
        return userPrefrenceStore.lastUsageDate
    }

    override suspend fun updateUsage(count: Int, date: String) {
        userPrefrenceStore.updateUsage(count = count, date = date)
    }
}

