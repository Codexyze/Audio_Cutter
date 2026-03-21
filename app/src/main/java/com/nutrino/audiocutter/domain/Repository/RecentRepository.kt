package com.nutrino.audiocutter.domain.Repository

import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
	suspend fun getAllRecentEntries(): Flow<ResultState<List<RecentTable>>>
	suspend fun upsertRecentEntry(recentTable: RecentTable): Flow<ResultState<String>>
	suspend fun deleteRecentEntry(recentTable: RecentTable): Flow<ResultState<String>>
	suspend fun getRecentEntriesByFeatureType(featureType: String): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByDateModifiedAsc(): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByDateModifiedDesc(): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByOutputNameAsc(): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByOutputNameDesc(): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByInputNameAsc(): Flow<ResultState<List<RecentTable>>>
	suspend fun getRecentEntriesByInputNameDesc(): Flow<ResultState<List<RecentTable>>>

	suspend fun upsertCropSegment(cropSegmentTable: CropSegmentTable): Flow<ResultState<String>>
	suspend fun getRecentCroppedSegmentFiles(): Flow<ResultState<List<String>>>
	suspend fun getRecentCropByFileType(fileType: String): Flow<ResultState<List<CropSegmentTable>>>
	suspend fun getCropSegmentsByFileName(fileName: String): Flow<ResultState<List<CropSegmentTable>>>
	suspend fun deleteRecentCroppedSegment(cropSegmentTable: CropSegmentTable): Flow<ResultState<String>>
}