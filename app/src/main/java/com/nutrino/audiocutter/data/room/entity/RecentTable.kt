package com.nutrino.audiocutter.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.nutrino.audiocutter.Constants.RoomConstants


@Entity(tableName = RoomConstants.RECENT_TABLE_NAME)
data class RecentTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "featureType")
    val featureType: String,
    @ColumnInfo(name = "inputUri")
    val inputUri: String,
    @ColumnInfo(name = "outputUri")
    val outputUri: String,
    @ColumnInfo(name = "date_modified")
    val date_modified: String,
    @ColumnInfo(name = "input_duration")
    val input_duration: String,
    @ColumnInfo(name = "output_duration")
    val output_duration: String,
    @ColumnInfo(name = "input_name")
    val input_name: String,
    @ColumnInfo(name = "output_name")
    val output_name: String,
    @ColumnInfo(name = "input_size")
    val input_size: String,
    @ColumnInfo(name = "output_size")
    val output_size: String,
    @ColumnInfo(name = "fileType")
    val fileType: String
)
