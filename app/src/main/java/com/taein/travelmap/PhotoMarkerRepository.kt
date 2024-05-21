package com.taein.travelmap

import com.taein.travelmap.database.PhotoMarkerDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoMarkerRepository @Inject constructor(
    private val photoMarkerDao: PhotoMarkerDao
) {
    suspend fun addPhotoMarker(photoMarker: PhotoMarker) {
        photoMarkerDao.insertPhotoMarker(photoMarker)
    }

    suspend fun getPhotoMarkers(): Flow<List<PhotoMarker>> {
        return photoMarkerDao.getAllPhotoMarkers()
    }
}