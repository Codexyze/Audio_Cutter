package com.nutrino.audiocutter.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nutrino.audiocutter.data.room.dao.CropSegmentDao
import com.nutrino.audiocutter.data.room.dao.RecentTableDao
import com.nutrino.audiocutter.data.room.entity.CropSegmentTable
import com.nutrino.audiocutter.data.room.entity.RecentTable

@Database(entities = [RecentTable::class, CropSegmentTable::class ], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    abstract fun recentTableDao(): RecentTableDao
    abstract fun recentcropSegmentDao(): CropSegmentDao

}