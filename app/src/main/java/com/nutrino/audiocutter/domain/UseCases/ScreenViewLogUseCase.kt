package com.nutrino.audiocutter.domain.UseCases

import android.os.Bundle
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import javax.inject.Inject

class ScreenViewLogUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(screenName: String, bundle: Bundle) {
        repository.screenViewLog(screenName, bundle)
    }
}
