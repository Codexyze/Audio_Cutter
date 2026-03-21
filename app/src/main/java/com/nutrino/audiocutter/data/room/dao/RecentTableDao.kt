package com.nutrino.audiocutter.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import com.nutrino.audiocutter.data.room.entity.RecentTable
import com.nutrino.audiocutter.Constants.RoomConstants

@Dao
interface RecentTableDao {
    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME}")
    fun getAllRecentEntries(): Flow<List<RecentTable>>
    
    @Upsert
    fun upsertRecentEntry(recentTable: RecentTable)
    
    @Delete
    fun deleteRecentEntry(recentTable: RecentTable)
    
    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} WHERE featureType = :featureType")
    fun getRecentEntriesByFeatureType(featureType: String): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY date_modified ASC")
    fun getRecentEntriesByDateModifiedAsc(): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY date_modified DESC")
    fun getRecentEntriesByDateModifiedDesc(): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY output_name ASC")
    fun getRecentEntriesByOutputNameAsc(): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY output_name DESC")
    fun getRecentEntriesByOutputNameDesc(): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY input_name ASC")
    fun getRecentEntriesByInputNameAsc(): Flow<List<RecentTable>>

    @Query("SELECT * FROM ${RoomConstants.RECENT_TABLE_NAME} ORDER BY input_name DESC")
    fun getRecentEntriesByInputNameDesc(): Flow<List<RecentTable>>
}