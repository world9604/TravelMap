package com.taein.travelmap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taein.travelmap.model.PhotoMarkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoMarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoMarker(photoMarker: PhotoMarkerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoMarkers(photoMarkers: List<PhotoMarkerEntity>)

    @Query("SELECT * FROM photo_markers")
    fun getAllPhotoMarkers(): Flow<List<PhotoMarkerEntity>>
}
