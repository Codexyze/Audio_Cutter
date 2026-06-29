package com.nutrino.audiocutter.domain.UseCases.userPref

import com.nutrino.audiocutter.domain.Repository.UserPrefRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class IncrementUsageUseCase @Inject constructor(
    private val userPrefRepository: UserPrefRepository
) {
    suspend operator fun invoke() {
        val currentCount = userPrefRepository.getUsageCount().first()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        userPrefRepository.updateUsage(currentCount + 1, today)
    }
}
