package com.taein.travelmap.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taein.travelmap.database.util.UriTypeConverter
import com.taein.travelmap.model.DiaryEntity
import com.taein.travelmap.model.PhotoMarkerEntity

@Database(entities = [PhotoMarkerEntity::class, DiaryEntity::class], version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoMarkerDao(): PhotoMarkerDao
    abstract fun diaryDao(): DiaryDao
}
