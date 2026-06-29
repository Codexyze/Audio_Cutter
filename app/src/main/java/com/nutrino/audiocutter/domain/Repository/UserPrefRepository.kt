package com.nutrino.audiocutter.domain.Repository

import kotlinx.coroutines.flow.Flow

interface UserPrefRepository {
    fun getThemeSelection(): Flow<String>
    suspend fun updateThemeSelection(theme: String)
    fun getUsageCount(): Flow<Int>
    fun getLastUsageDate(): Flow<String>
    suspend fun updateUsage(count: Int, date: String)
}

