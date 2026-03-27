package com.nutrino.audiocutter.domain.StateHandeling

import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.data.DataClass.Video
import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.revenuecat.purchases.Package

sealed class ResultState<out T>{
    object Loading: ResultState<Nothing>()
    data class Success<T>(val data: T): ResultState<T>()
    data class Error(val message: String): ResultState<Nothing>()
}

data class AudioTrimmerState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class VideoTrimmerState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class AudioExtractorState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class GetAllSongState(
    val isLoading: Boolean = false,
    val data: List<Song> = emptyList(),
    val error: String ? = null
)

data class GetAllVideoState(
    val isLoading: Boolean = false,
    val data: List<Video> = emptyList(),
    val error: String ? = null
)

data class GetAllSongsForMergeState(
    val isLoading: Boolean = false,
    val data: List<Song> = emptyList(),
    val error: String ? = null
)

data class AudioMergeState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class MultiCropAudioState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class MultiCropVideoState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class ConvertAudioFormatState(
    val isLoading: Boolean = false,
    val data: String= "",
    val error: String ? = null
)

data class RecordAudioState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String? = null,
    val isRecording: Boolean = false,
    val isPaused: Boolean = false
)

data class AdState(
    val isLoading: Boolean = false,
    val isAdReady: Boolean = false,
    val isAdShowing: Boolean = false,
    val error: String? = null,
    val adDismissed: Boolean = false
)

data class GetAllRecentEntriesState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class UpsertRecentEntryState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String? = null
)

data class DeleteRecentEntryState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String? = null
)

data class GetRecentEntriesByFeatureTypeState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByDateModifiedAscState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByDateModifiedDescState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByOutputNameAscState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByOutputNameDescState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByInputNameAscState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class GetRecentEntriesByInputNameDescState(
    val isLoading: Boolean = false,
    val data: List<RecentTable> = emptyList(),
    val error: String? = null
)

data class UpsertCropSegmentState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String? = null
)

data class GetRecentCroppedSegmentFilesState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val error: String? = null
)

data class GetRecentCropByFileTypeState(
    val isLoading: Boolean = false,
    val data: List<CropSegmentTable> = emptyList(),
    val error: String? = null
)

data class GetCropSegmentsByFileNameState(
    val isLoading: Boolean = false,
    val data: List<CropSegmentTable> = emptyList(),
    val error: String? = null
)

data class DeleteRecentCroppedSegmentState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String? = null
)

data class GetAllPackageState(
    val isLoading: Boolean = false ,
    val data: List<Package> = emptyList(),
    val error : String? = null
)

data class IsUserProState(
    val isLoading: Boolean = false,
    val data: Boolean = false,
    val error: String? = null
)

