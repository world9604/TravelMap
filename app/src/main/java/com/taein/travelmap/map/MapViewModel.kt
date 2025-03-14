package com.taein.travelmap.map

import android.content.Context
import android.location.LocationManager
import android.net.Uri
import android.os.CancellationSignal
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taein.travelmap.R
import com.taein.travelmap.repository.diary.DiaryRepository
import com.taein.travelmap.repository.photoMarker.PhotoMarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random


@HiltViewModel
class MapViewModel @Inject constructor(
    private val photoMarkerRepository: PhotoMarkerRepository,
    private val diaryRepository: DiaryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.PhotoNotReady)
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        viewModelScope.launch {
            photoMarkerRepository.observeAll()
                .map { photoMarkers -> photoMarkers.map { photoMarker ->
                        val diary = diaryRepository.observe(photoMarker.id).firstOrNull()
                        if (!diary?.contents.isNullOrBlank()) {
                            photoMarker.copy(
                                markerTitle = context.getString(R.string.photo_marker_no_content_text))
                        } else {
                            photoMarker
                        }
                    }
                }.map { updatedPhotoMarkers ->
                    if (updatedPhotoMarkers.isEmpty()) MapUiState.PhotoNotLoad
                    else MapUiState.Success(updatedPhotoMarkers)
                }.collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    fun processImageUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val currentLocation = getCurrentLocation(context)
                if (currentLocation != null) {
                    val (latitude, longitude) = currentLocation
                    addLocationToImage(context, uri, latitude, longitude)
                    createUserPhoto(context, uri)?.let {
                        photoMarkerRepository.addPhotoMarker(it)
                    }
                } else {
                    _uiState.value = MapUiState.PhotoNotLoad
                    return@launch
                }
            } catch (e: SecurityException) {
                // 권한 오류 처리
            }
        }
    }

    fun processImageUri(context: Context, uriList: List<Uri>) {
        _uiState.value = MapUiState.Loading

        val userPhotoList = uriList.mapNotNull { uri ->
            createUserPhoto(context, uri)
        }

        if (userPhotoList.isNotEmpty()) {
            viewModelScope.launch {
                photoMarkerRepository.addPhotoMarkers(userPhotoList)
            }
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
                        markerTitle = "",
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

    /*@SuppressLint("MissingPermission")
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
    }*/

    private suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val cancellationSignal = CancellationSignal()

        return suspendCancellableCoroutine { continuation ->
            try {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    cancellationSignal,
                    context.mainExecutor
                ) { location ->
                    if (location != null) {
                        continuation.resume(Pair(location.latitude, location.longitude))
                    } else {
                        continuation.resume(null)
                    }
                }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }

            continuation.invokeOnCancellation {
                cancellationSignal.cancel()
            }
        }
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
