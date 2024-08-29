package com.taein.travelmap.map

sealed interface MapUiState {
    data object Loading : MapUiState
    data object PhotoNotLoad : MapUiState

    /**
     * PhotoNotReady 상태는 최초의 photo marker를 한번도 업로드 하지 않은 상태
     */
    data object PhotoNotReady : MapUiState
    data class Success(
        val photoMarker: List<PhotoMarker> = emptyList()
    ) : MapUiState {
        fun isEmpty(): Boolean = photoMarker.isEmpty()
    }
    data class Error(val message: String) : MapUiState
}