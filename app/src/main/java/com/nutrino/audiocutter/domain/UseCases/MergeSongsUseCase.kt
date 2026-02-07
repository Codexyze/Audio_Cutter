package com.nutrino.audiocutter.domain.UseCases

import android.net.Uri
import com.nutrino.audiocutter.domain.Repository.GetAllSongRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MergeSongsUseCase @Inject constructor(private val repository: GetAllSongRepository) {
    suspend operator fun invoke(uriList: List<Uri>, filename: String): Flow<ResultState<String>> {
        return repository.mergeSongs(uriList = uriList, filename = filename)
    }
}
