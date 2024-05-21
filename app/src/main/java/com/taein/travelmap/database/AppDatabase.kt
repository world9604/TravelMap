package com.taein.travelmap.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taein.travelmap.PhotoMarker
import com.taein.travelmap.database.util.UriTypeConverter

@Database(entities = [PhotoMarker::class], version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoMarkerDao(): PhotoMarkerDao
}
