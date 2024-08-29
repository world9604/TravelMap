package com.taein.travelmap.repository.photoMarker

import com.taein.travelmap.map.PhotoMarker
import kotlinx.coroutines.flow.Flow

interface PhotoMarkerRepository {

    suspend fun addPhotoMarker(photoMarker: PhotoMarker)

    suspend fun addPhotoMarkers(photoMarkers: List<PhotoMarker>)

    fun observeAll(): Flow<List<PhotoMarker>>
}