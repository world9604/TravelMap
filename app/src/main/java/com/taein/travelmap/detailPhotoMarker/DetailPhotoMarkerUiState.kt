package com.taein.travelmap.detailPhotoMarker

sealed interface DetailPhotoMarkerUiState {
    data object Loading : DetailPhotoMarkerUiState
    data object NotShown : DetailPhotoMarkerUiState
    data class PhotoUploadSuccess(
        val diary: Diary
    ) : DetailPhotoMarkerUiState
    data class Error(val message: String) : DetailPhotoMarkerUiState
}