package com.nutrino.audiocutter.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.nutrino.audiocutter.Constants.RoomConstants

@Entity(tableName = RoomConstants.CROP_SEGMENT_TABLE_NAME)
data class CropSegmentTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    @ColumnInfo(name = "start")
    val start: String,
    @ColumnInfo(name = "end")
    val end: String,
    @ColumnInfo(name = "fileName")
    val fileName: String,
    @ColumnInfo(name = "featureType")
    val featureType: String,
    @ColumnInfo(name = "fileType")
    val fileType: String,
    @ColumnInfo(name = "input_uri")
    val input_uri: String
)
