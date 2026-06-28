package com.nutrino.audiocutter.domain.UseCases

import android.os.Bundle
import com.nutrino.audiocutter.domain.Repository.AnalyticsRepository
import javax.inject.Inject

class LogEventUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(eventName: String, params: Bundle?) {
        repository.logEvents(eventName, params)
    }
}
