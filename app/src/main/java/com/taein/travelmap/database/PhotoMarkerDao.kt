package com.taein.travelmap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taein.travelmap.model.PhotoMarkerEntity

@Dao
interface PhotoMarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoMarker(photoMarker: PhotoMarkerEntity)

    @Query("SELECT * FROM photo_markers")
    suspend fun getAllPhotoMarkers(): List<PhotoMarkerEntity>
}
