package com.nutrino.audiocutter.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.domain.StateHandeling.DeleteRecentCroppedSegmentState
import com.nutrino.audiocutter.domain.StateHandeling.DeleteRecentEntryState
import com.nutrino.audiocutter.domain.StateHandeling.GetAllRecentEntriesState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentCropByFileTypeState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentCroppedSegmentFilesState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByDateModifiedAscState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByDateModifiedDescState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByFeatureTypeState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByInputNameAscState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByInputNameDescState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByOutputNameAscState
import com.nutrino.audiocutter.domain.StateHandeling.GetRecentEntriesByOutputNameDescState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.StateHandeling.UpsertCropSegmentState
import com.nutrino.audiocutter.domain.StateHandeling.UpsertRecentEntryState
import com.nutrino.audiocutter.domain.UseCases.recent.DeleteRecentCroppedSegmentUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.DeleteRecentEntryUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetAllRecentEntriesUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentCropByFileTypeUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentCroppedSegmentFilesUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByDateModifiedAscUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByDateModifiedDescUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByFeatureTypeUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByInputNameAscUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByInputNameDescUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByOutputNameAscUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.GetRecentEntriesByOutputNameDescUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.UpsertCropSegmentUseCase
import com.nutrino.audiocutter.domain.UseCases.recent.UpsertRecentEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val getAllRecentEntriesUseCase: GetAllRecentEntriesUseCase,
    private val upsertRecentEntryUseCase: UpsertRecentEntryUseCase,
    private val deleteRecentEntryUseCase: DeleteRecentEntryUseCase,
    private val getRecentEntriesByFeatureTypeUseCase: GetRecentEntriesByFeatureTypeUseCase,
    private val getRecentEntriesByDateModifiedAscUseCase: GetRecentEntriesByDateModifiedAscUseCase,
    private val getRecentEntriesByDateModifiedDescUseCase: GetRecentEntriesByDateModifiedDescUseCase,
    private val getRecentEntriesByOutputNameAscUseCase: GetRecentEntriesByOutputNameAscUseCase,
    private val getRecentEntriesByOutputNameDescUseCase: GetRecentEntriesByOutputNameDescUseCase,
    private val getRecentEntriesByInputNameAscUseCase: GetRecentEntriesByInputNameAscUseCase,
    private val getRecentEntriesByInputNameDescUseCase: GetRecentEntriesByInputNameDescUseCase,
    private val upsertCropSegmentUseCase: UpsertCropSegmentUseCase,
    private val getRecentCroppedSegmentFilesUseCase: GetRecentCroppedSegmentFilesUseCase,
    private val getRecentCropByFileTypeUseCase: GetRecentCropByFileTypeUseCase,
    private val deleteRecentCroppedSegmentUseCase: DeleteRecentCroppedSegmentUseCase
) : ViewModel() {

    private val _getAllRecentEntriesState = MutableStateFlow(GetAllRecentEntriesState())
    val getAllRecentEntriesState = _getAllRecentEntriesState.asStateFlow()

    private val _upsertRecentEntryState = MutableStateFlow(UpsertRecentEntryState())
    val upsertRecentEntryState = _upsertRecentEntryState.asStateFlow()

    private val _deleteRecentEntryState = MutableStateFlow(DeleteRecentEntryState())
    val deleteRecentEntryState = _deleteRecentEntryState.asStateFlow()

    private val _getRecentEntriesByFeatureTypeState = MutableStateFlow(GetRecentEntriesByFeatureTypeState())
    val getRecentEntriesByFeatureTypeState = _getRecentEntriesByFeatureTypeState.asStateFlow()

    private val _getRecentEntriesByDateModifiedAscState = MutableStateFlow(GetRecentEntriesByDateModifiedAscState())
    val getRecentEntriesByDateModifiedAscState = _getRecentEntriesByDateModifiedAscState.asStateFlow()

    private val _getRecentEntriesByDateModifiedDescState = MutableStateFlow(GetRecentEntriesByDateModifiedDescState())
    val getRecentEntriesByDateModifiedDescState = _getRecentEntriesByDateModifiedDescState.asStateFlow()

    private val _getRecentEntriesByOutputNameAscState = MutableStateFlow(GetRecentEntriesByOutputNameAscState())
    val getRecentEntriesByOutputNameAscState = _getRecentEntriesByOutputNameAscState.asStateFlow()

    private val _getRecentEntriesByOutputNameDescState = MutableStateFlow(GetRecentEntriesByOutputNameDescState())
    val getRecentEntriesByOutputNameDescState = _getRecentEntriesByOutputNameDescState.asStateFlow()

    private val _getRecentEntriesByInputNameAscState = MutableStateFlow(GetRecentEntriesByInputNameAscState())
    val getRecentEntriesByInputNameAscState = _getRecentEntriesByInputNameAscState.asStateFlow()

    private val _getRecentEntriesByInputNameDescState = MutableStateFlow(GetRecentEntriesByInputNameDescState())
    val getRecentEntriesByInputNameDescState = _getRecentEntriesByInputNameDescState.asStateFlow()

    private val _upsertCropSegmentState = MutableStateFlow(UpsertCropSegmentState())
    val upsertCropSegmentState = _upsertCropSegmentState.asStateFlow()

    private val _getRecentCroppedSegmentFilesState = MutableStateFlow(GetRecentCroppedSegmentFilesState())
    val getRecentCroppedSegmentFilesState = _getRecentCroppedSegmentFilesState.asStateFlow()

    private val _getRecentCropByFileTypeState = MutableStateFlow(GetRecentCropByFileTypeState())
    val getRecentCropByFileTypeState = _getRecentCropByFileTypeState.asStateFlow()

    private val _deleteRecentCroppedSegmentState = MutableStateFlow(DeleteRecentCroppedSegmentState())
    val deleteRecentCroppedSegmentState = _deleteRecentCroppedSegmentState.asStateFlow()

    fun getAllRecentEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllRecentEntriesUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getAllRecentEntriesState.value = GetAllRecentEntriesState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getAllRecentEntriesState.value = GetAllRecentEntriesState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getAllRecentEntriesState.value = GetAllRecentEntriesState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun upsertRecentEntry(recentTable: RecentTable) {
        viewModelScope.launch(Dispatchers.IO) {
            upsertRecentEntryUseCase.invoke(recentTable = recentTable).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _upsertRecentEntryState.value = UpsertRecentEntryState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _upsertRecentEntryState.value = UpsertRecentEntryState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _upsertRecentEntryState.value = UpsertRecentEntryState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteRecentEntry(recentTable: RecentTable) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteRecentEntryUseCase.invoke(recentTable = recentTable).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _deleteRecentEntryState.value = DeleteRecentEntryState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _deleteRecentEntryState.value = DeleteRecentEntryState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _deleteRecentEntryState.value = DeleteRecentEntryState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByFeatureType(featureType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByFeatureTypeUseCase.invoke(featureType = featureType).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByFeatureTypeState.value = GetRecentEntriesByFeatureTypeState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByFeatureTypeState.value = GetRecentEntriesByFeatureTypeState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByFeatureTypeState.value = GetRecentEntriesByFeatureTypeState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByDateModifiedAsc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByDateModifiedAscUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByDateModifiedAscState.value = GetRecentEntriesByDateModifiedAscState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByDateModifiedAscState.value = GetRecentEntriesByDateModifiedAscState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByDateModifiedAscState.value = GetRecentEntriesByDateModifiedAscState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByDateModifiedDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByDateModifiedDescUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByDateModifiedDescState.value = GetRecentEntriesByDateModifiedDescState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByDateModifiedDescState.value = GetRecentEntriesByDateModifiedDescState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByDateModifiedDescState.value = GetRecentEntriesByDateModifiedDescState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByOutputNameAsc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByOutputNameAscUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByOutputNameAscState.value = GetRecentEntriesByOutputNameAscState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByOutputNameAscState.value = GetRecentEntriesByOutputNameAscState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByOutputNameAscState.value = GetRecentEntriesByOutputNameAscState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByOutputNameDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByOutputNameDescUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByOutputNameDescState.value = GetRecentEntriesByOutputNameDescState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByOutputNameDescState.value = GetRecentEntriesByOutputNameDescState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByOutputNameDescState.value = GetRecentEntriesByOutputNameDescState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByInputNameAsc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByInputNameAscUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByInputNameAscState.value = GetRecentEntriesByInputNameAscState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByInputNameAscState.value = GetRecentEntriesByInputNameAscState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByInputNameAscState.value = GetRecentEntriesByInputNameAscState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentEntriesByInputNameDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentEntriesByInputNameDescUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentEntriesByInputNameDescState.value = GetRecentEntriesByInputNameDescState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentEntriesByInputNameDescState.value = GetRecentEntriesByInputNameDescState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentEntriesByInputNameDescState.value = GetRecentEntriesByInputNameDescState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun upsertCropSegment(cropSegmentTable: CropSegmentTable) {
        viewModelScope.launch(Dispatchers.IO) {
            upsertCropSegmentUseCase.invoke(cropSegmentTable = cropSegmentTable).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _upsertCropSegmentState.value = UpsertCropSegmentState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _upsertCropSegmentState.value = UpsertCropSegmentState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _upsertCropSegmentState.value = UpsertCropSegmentState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentCroppedSegmentFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentCroppedSegmentFilesUseCase.invoke().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentCroppedSegmentFilesState.value = GetRecentCroppedSegmentFilesState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentCroppedSegmentFilesState.value = GetRecentCroppedSegmentFilesState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentCroppedSegmentFilesState.value = GetRecentCroppedSegmentFilesState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getRecentCropByFileType(fileType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentCropByFileTypeUseCase.invoke(fileType = fileType).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getRecentCropByFileTypeState.value = GetRecentCropByFileTypeState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getRecentCropByFileTypeState.value = GetRecentCropByFileTypeState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _getRecentCropByFileTypeState.value = GetRecentCropByFileTypeState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteRecentCroppedSegment(cropSegmentTable: CropSegmentTable) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteRecentCroppedSegmentUseCase.invoke(cropSegmentTable = cropSegmentTable).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _deleteRecentCroppedSegmentState.value = DeleteRecentCroppedSegmentState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _deleteRecentCroppedSegmentState.value = DeleteRecentCroppedSegmentState(
                            isLoading = false,
                            data = result.data
                        )
                    }
                    is ResultState.Error -> {
                        _deleteRecentCroppedSegmentState.value = DeleteRecentCroppedSegmentState(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}

