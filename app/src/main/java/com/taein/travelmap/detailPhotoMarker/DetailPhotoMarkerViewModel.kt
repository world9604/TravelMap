package com.taein.travelmap.detailPhotoMarker


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taein.travelmap.repository.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class DetailPhotoMarkerViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailPhotoMarkerUiState>(DetailPhotoMarkerUiState.NotShown)
    val uiState = repository.observeAll()
        .map{ diaries ->
            if (diaries.isEmpty()) DetailPhotoMarkerUiState.NotShown
            else DetailPhotoMarkerUiState.PhotoUploadSuccess(diaries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailPhotoMarkerUiState.Loading,
        )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun getFormattedDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateFormatter)
    }
}
