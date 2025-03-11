package com.taein.travelmap.map

import android.net.Uri
import com.taein.travelmap.model.PhotoMarkerEntity
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

data class PhotoMarker(
    val id: String,
    val uri: Uri,
    val markerTitle: String,
    val gpsLatitude: Double,
    val gpsLongitude: Double
) : TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(gpsLatitude, gpsLongitude)
    }

}

fun PhotoMarker.asEntity() = PhotoMarkerEntity(
    id = id,
    uri = uri,
    gpsLatitude = gpsLatitude,
    gpsLongitude = gpsLongitude
)