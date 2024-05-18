package com.taein.travelmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

sealed interface MapUiState {
    data object Loading : MapUiState
    data object PhotoNotLoad : MapUiState
    data object PhotoNotReady : MapUiState
    data class Success(
        val userPhoto: List<UserPhoto> = emptyList()
    ) : MapUiState {
        fun isEmpty(): Boolean = userPhoto.isEmpty()
    }
    data class Error(val message: String) : MapUiState
}

data class UserPhoto(
    val id: String,
    val uri: Uri,
    val gpsLatitude: Double,
    val gpsLongitude: Double
)

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.PhotoNotReady)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun processImageUri(
        context: Context,
        uri: Uri
    ) {
        _uiState.value = MapUiState.Loading

        val userPhoto = runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exifInterface = ExifInterface(inputStream)
                val latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                    convertToDegree(it).toDouble()
                }
                val longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                    convertToDegree(it).toDouble()
                }

                if (latitude != null && longitude != null) {
                    UserPhoto(
                        id = Random.nextLong().toString(),
                        uri = uri,
                        gpsLatitude = latitude,
                        gpsLongitude = longitude
                    )
                } else null
            }
        }.onFailure { e ->
            e.printStackTrace()
            _uiState.value = MapUiState.Error("Failed to process image. message : ${e.message}")
            return
        }.getOrNull()

        if (userPhoto != null) {
            _uiState.value = MapUiState.Success(listOf(userPhoto))
            return
        }

        if (_uiState.value is MapUiState.Loading) {
            _uiState.value = MapUiState.PhotoNotLoad
        }
    }

    fun processImageUri(
        context: Context,
        uriList: List<Uri>
    ) {
        _uiState.value = MapUiState.Loading

        val userPhotoList = uriList.mapNotNull { uri ->
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    val latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                        convertToDegree(it).toDouble()
                    }
                    val longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                        convertToDegree(it).toDouble()
                    }

                    if (latitude != null && longitude != null) {
                        UserPhoto(
                            id = Random.nextLong().toString(),
                            uri = uri,
                            gpsLatitude = latitude,
                            gpsLongitude = longitude
                        )
                    } else null
                }
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull()
        }

        _uiState.value = if (userPhotoList.isNotEmpty()) {
            MapUiState.Success(userPhotoList)
        } else {
            MapUiState.PhotoNotLoad
        }
    }

    private fun convertToDegree(stringDMS: String): Float {
        val DMS = stringDMS.split(",")
        val (D0, D1) = DMS[0].split("/").map { it.toDouble() }
        val (M0, M1) = DMS[1].split("/").map { it.toDouble() }
        val (S0, S1) = DMS[2].split("/").map { it.toDouble() }

        return (D0 / D1 + M0 / M1 / 60 + S0 / S1 / 3600).toFloat()
    }

    fun resizeBitmap(uri: Uri, context: Context, targetWidth: Int, targetHeight: Int): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)

                options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
                options.inJustDecodeBounds = false

                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val (halfHeight, halfWidth) = height / 2 to width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
