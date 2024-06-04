package com.taein.travelmap.model

import android.net.Uri

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