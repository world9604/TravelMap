package com.taein.travelmap.repository

import com.taein.travelmap.database.PhotoMarkerDao
import com.taein.travelmap.model.PhotoMarker
import com.taein.travelmap.model.PhotoMarkerEntity
import com.taein.travelmap.model.asEntity
import com.taein.travelmap.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstPhotoMarkerRepository @Inject constructor(
    private val photoMarkerDao: PhotoMarkerDao
) : PhotoMarkerRepository {

    override suspend fun addPhotoMarker(photoMarker: PhotoMarker) {
        photoMarkerDao.insertPhotoMarker(photoMarker.asEntity())
    }

    override suspend fun addPhotoMarkers(photoMarkers: List<PhotoMarker>) {
        photoMarkerDao.insertPhotoMarkers(photoMarkers.map(PhotoMarker::asEntity))
    }

    override fun observeAll(): Flow<List<PhotoMarker>> =
        photoMarkerDao.getAllPhotoMarkers()
            .map { it.map(PhotoMarkerEntity::asExternalModel) }
}