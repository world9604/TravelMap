package com.taein.travelmap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

sealed interface MapUiState {
    data object Loading : MapUiState
    data object PhotoNotLoad : MapUiState
    data object PhotoNotReady : MapUiState
    data class Success(
        val photoMarker: List<PhotoMarker> = emptyList()
    ) : MapUiState {
        fun isEmpty(): Boolean = photoMarker.isEmpty()
    }
    data class Error(val message: String) : MapUiState
}

data class PhotoMarker(
    val id: String,
    val uri: Uri,
    val gpsLatitude: Double,
    val gpsLongitude: Double
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: PhotoMarkerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.PhotoNotReady)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun processImageUri(context: Context, uri: Uri) {
        _uiState.value = MapUiState.Loading

        val currentLocation = getCurrentLocation(context)
        if (currentLocation != null) {
            val (latitude, longitude) = currentLocation
            addLocationToImage(context, uri, latitude, longitude)
        } else {
            _uiState.value = MapUiState.PhotoNotLoad
            return
        }

        val userPhoto = createUserPhoto(context, uri)
        userPhoto?.let {
            viewModelScope.launch {
                repository.addPhotoMarker(it)
                _uiState.value = MapUiState.Success(listOf(it))
            }
        } ?: run {
            _uiState.value = MapUiState.PhotoNotLoad
        }
    }

    fun processImageUri(context: Context, uriList: List<Uri>) {
        _uiState.value = MapUiState.Loading

        val userPhotoList = uriList.mapNotNull { uri ->
            createUserPhoto(context, uri)
        }

        _uiState.value = if (userPhotoList.isNotEmpty()) {
            MapUiState.Success(userPhotoList)
        } else {
            MapUiState.PhotoNotLoad
        }
    }

    private fun createUserPhoto(context: Context, uri: Uri): PhotoMarker? {
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exifInterface = ExifInterface(inputStream)
                val latLong = exifInterface.latLong

                latLong?.let {
                    PhotoMarker(
                        id = Random.nextLong().toString(),
                        uri = uri,
                        gpsLatitude = it[0],
                        gpsLongitude = it[1]
                    )
                }
            }
        }.onFailure { e ->
            e.printStackTrace()
            _uiState.value = MapUiState.Error("Failed to process image. message : ${e.message}")
        }.getOrNull()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val location: Location? = when {
            isGpsEnabled -> locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            isNetworkEnabled -> locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            else -> null
        }

        return location?.let { Pair(it.latitude, it.longitude) }
    }

    private fun addLocationToImage(context: Context, imageUri: Uri, latitude: Double, longitude: Double) {
        try {
            context.contentResolver.openFileDescriptor(imageUri, "rw")?.use { pfd ->
                val exif = ExifInterface(pfd.fileDescriptor)
                exif.setLatLong(latitude, longitude)
                exif.saveAttributes()
                Log.d("EXIF", "Location added to image: $latitude, $longitude")
            }
        } catch (e: Exception) {
            Log.e("EXIF", "Failed to add location to image", e)
        }
    }
}
