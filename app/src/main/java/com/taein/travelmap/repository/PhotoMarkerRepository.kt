package com.taein.travelmap.repository

import com.taein.travelmap.model.PhotoMarker
import kotlinx.coroutines.flow.Flow

interface PhotoMarkerRepository {

    suspend fun addPhotoMarker(photoMarker: PhotoMarker)

    suspend fun addPhotoMarkers(photoMarkers: List<PhotoMarker>)

    fun observeAll(): Flow<List<PhotoMarker>>
}