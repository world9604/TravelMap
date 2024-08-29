package com.taein.travelmap.map

import android.net.Uri
import com.taein.travelmap.model.PhotoMarkerEntity

data class PhotoMarker(
    val id: String,
    val uri: Uri,
    val gpsLatitude: Double,
    val gpsLongitude: Double
)

fun PhotoMarker.asEntity() = PhotoMarkerEntity(
    id = id,
    uri = uri,
    gpsLatitude = gpsLatitude,
    gpsLongitude = gpsLongitude
)