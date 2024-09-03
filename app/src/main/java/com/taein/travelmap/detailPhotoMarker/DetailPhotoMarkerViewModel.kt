package com.taein.travelmap.detailPhotoMarker


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taein.travelmap.repository.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class DetailPhotoMarkerViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailPhotoMarkerUiState>(DetailPhotoMarkerUiState.NotShown)
    val uiState = repository.observe(id)
        .map { diary ->
            DetailPhotoMarkerUiState.PhotoUploadSuccess(diary)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailPhotoMarkerUiState.Loading,
        )

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun getFormattedDate(calendar: Calendar): String {
        return dateFormatter.format(calendar.time)
    }
}
