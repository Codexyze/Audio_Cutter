package com.nutrino.audiocutter.data.RepoImpl

import android.util.Log
import com.nutrino.audiocutter.data.room.database.AppDataBase
import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.domain.Repository.RecentRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecentRepositoryImpl @Inject constructor(
    private val appDataBase: AppDataBase
): RecentRepository {
    private companion object {
        const val TAG = "RecentRepository"
    }

    override suspend fun getAllRecentEntries(): Flow<ResultState<List<RecentTable>>> = flow{
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getAllRecentEntries().collect { data ->
                emit(ResultState.Success(data = data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllRecentEntries failed: ${e.message}", e)
            emit(ResultState.Error("[getAllRecentEntries] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun upsertRecentEntry(recentTable: RecentTable): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().upsertRecentEntry(recentTable)
            emit(ResultState.Success("Recent entry saved successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "upsertRecentEntry failed: ${e.message}", e)
            emit(ResultState.Error("[upsertRecentEntry] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun deleteRecentEntry(recentTable: RecentTable): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().deleteRecentEntry(recentTable)
            emit(ResultState.Success("Recent entry deleted successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "deleteRecentEntry failed: ${e.message}", e)
            emit(ResultState.Error("[deleteRecentEntry] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByFeatureType(featureType: String): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByFeatureType(featureType).collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByFeatureType failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByFeatureType] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByDateModifiedAsc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByDateModifiedAsc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByDateModifiedAsc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByDateModifiedAsc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByDateModifiedDesc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByDateModifiedDesc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByDateModifiedDesc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByDateModifiedDesc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByOutputNameAsc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByOutputNameAsc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByOutputNameAsc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByOutputNameAsc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByOutputNameDesc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByOutputNameDesc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByOutputNameDesc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByOutputNameDesc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByInputNameAsc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByInputNameAsc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByInputNameAsc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByInputNameAsc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentEntriesByInputNameDesc(): Flow<ResultState<List<RecentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentTableDao().getRecentEntriesByInputNameDesc().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentEntriesByInputNameDesc failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentEntriesByInputNameDesc] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun upsertCropSegment(cropSegmentTable: CropSegmentTable): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentcropSegmentDao().upsertCropSegment(cropSegmentTable)
            emit(ResultState.Success("Crop segment saved successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "upsertCropSegment failed: ${e.message}", e)
            emit(ResultState.Error("[upsertCropSegment] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentCroppedSegmentFiles(): Flow<ResultState<List<String>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentcropSegmentDao().getRecentCroppedSegmentFiles().collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentCroppedSegmentFiles failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentCroppedSegmentFiles] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getRecentCropByFileType(fileType: String): Flow<ResultState<List<CropSegmentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentcropSegmentDao().getRecentCropByFileType(fileType).collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecentCropByFileType failed: ${e.message}", e)
            emit(ResultState.Error("[getRecentCropByFileType] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun getCropSegmentsByFileName(fileName: String): Flow<ResultState<List<CropSegmentTable>>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentcropSegmentDao().getCropSegmentsByFileName(fileName).collect { data ->
                emit(ResultState.Success(data))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCropSegmentsByFileName failed: ${e.message}", e)
            emit(ResultState.Error("[getCropSegmentsByFileName] ${e.message ?: "Something went wrong"}"))
        }
    }

    override suspend fun deleteRecentCroppedSegment(cropSegmentTable: CropSegmentTable): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            appDataBase.recentcropSegmentDao().deleteRecentCroppedSegment(cropSegmentTable)
            emit(ResultState.Success("Crop segment deleted successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "deleteRecentCroppedSegment failed: ${e.message}", e)
            emit(ResultState.Error("[deleteRecentCroppedSegment] ${e.message ?: "Something went wrong"}"))
        }
    }

}