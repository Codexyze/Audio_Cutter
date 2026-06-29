package com.nutrino.audiocutter.domain.UseCases.userPref

import com.nutrino.audiocutter.domain.Repository.UserPrefRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.revenueCat.IsUserProUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CheckFeatureLimitUseCase @Inject constructor(
    private val userPrefRepository: UserPrefRepository,
    private val isUserProUseCase: IsUserProUseCase
) {
    operator fun invoke(): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)

        // 1. Collect Pro status reactively
        isUserProUseCase.invoke().collect { isProResult ->
            when (isProResult) {
                is ResultState.Loading -> {
                    // Stay in loading while checking Pro status
                }
                is ResultState.Success -> {
                    val isPro = isProResult.data
                    if (isPro) {
                        emit(ResultState.Success(true))
                    } else {
                        // 2. Logic for Free Users - Only proceed if not Pro
                        val count = userPrefRepository.getUsageCount().first()
                        val lastDate = userPrefRepository.getLastUsageDate().first()
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                        if (lastDate != today) {
                            userPrefRepository.updateUsage(0, today)
                            emit(ResultState.Success(true))
                        } else if (count < 3) {
                            emit(ResultState.Success(true))
                        } else {
                            emit(ResultState.Error("Daily limit of 3 reached. Upgrade to Pro for unlimited use!"))
                        }
                    }
                }
                is ResultState.Error -> {
                    // If Pro check fails (e.g. no internet), fallback to Free User logic
                    val count = userPrefRepository.getUsageCount().first()
                    val lastDate = userPrefRepository.getLastUsageDate().first()
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    if (lastDate != today) {
                        userPrefRepository.updateUsage(0, today)
                        emit(ResultState.Success(true))
                    } else if (count < 3) {
                        emit(ResultState.Success(true))
                    } else {
                        emit(ResultState.Error("Daily limit of 3 reached. Upgrade to Pro for unlimited use!"))
                    }
                }
            }
        }
    }
}
