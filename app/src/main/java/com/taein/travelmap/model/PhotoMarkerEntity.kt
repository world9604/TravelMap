package com.taein.travelmap.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taein.travelmap.map.PhotoMarker

@Entity(tableName = "photo_markers")
data class PhotoMarkerEntity(
    @PrimaryKey val id: String,
    val uri: Uri,
    val gpsLatitude: Double,
    val gpsLongitude: Double
)

fun PhotoMarkerEntity.asExternalModel() = PhotoMarker(
    id = id,
    uri = uri,
    gpsLatitude = gpsLatitude,
    gpsLongitude = gpsLongitude
)