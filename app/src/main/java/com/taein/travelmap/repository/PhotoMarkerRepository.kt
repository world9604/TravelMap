package com.taein.travelmap.repository

import com.taein.travelmap.database.PhotoMarkerDao
import com.taein.travelmap.model.PhotoMarkerEntity
import javax.inject.Inject

class PhotoMarkerRepository @Inject constructor(
    private val photoMarkerDao: PhotoMarkerDao
) {
    suspend fun addPhotoMarker(photoMarker: PhotoMarkerEntity) {
        photoMarkerDao.insertPhotoMarker(photoMarker)
    }

    suspend fun getPhotoMarkers(): List<PhotoMarkerEntity> {
        return photoMarkerDao.getAllPhotoMarkers()
    }
}