package com.taein.travelmap.detailPhotoMarker


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taein.travelmap.markerIdArg
import com.taein.travelmap.repository.diary.DiaryRepository
import com.taein.travelmap.repository.photoMarker.PhotoMarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class DetailPhotoMarkerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val diaryRepository: DiaryRepository,
    private val photoMarkerRepository: PhotoMarkerRepository
) : ViewModel() {

    private val markerId: String = savedStateHandle[markerIdArg] ?: ""

    val detailPhotoMarkerUiState = diaryRepository.observe(markerId)
        .map { diary ->
            if (diary != null) {
                DetailPhotoMarkerUiState.PhotoUploadSuccess(diary)
            } else {
                val photoMarkers = photoMarkerRepository.observeAll()
                    .first()
                    .filter { it.id == markerId }

                if (photoMarkers.isNotEmpty()) {
                    val newDiary = Diary(
                        id = markerId,
                        photo = photoMarkers.map { it.uri },
                        date = getFormattedDate(Calendar.getInstance()),
                        contents = ""
                    )
                    diaryRepository.addDiary(newDiary)
                    DetailPhotoMarkerUiState.PhotoUploadSuccess(newDiary)
                } else {
                    DetailPhotoMarkerUiState.Error("Photo marker not found")
                }
            }
        }
        .catch { throwable ->
            emit(DetailPhotoMarkerUiState.Error(throwable.message ?: "Diary not found"))
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

    fun updateDiaryContents(newContents: String) {
        viewModelScope.launch {
            val currentDiary = (detailPhotoMarkerUiState.value as? DetailPhotoMarkerUiState.PhotoUploadSuccess)?.diary
            currentDiary?.let { diary ->
                val updatedDiary = diary.copy(contents = newContents)
                diaryRepository.updateDiary(updatedDiary)
            }
        }
    }

    fun deleteDiary(onDeleteComplete: () -> Unit) {
        viewModelScope.launch {
            diaryRepository.deleteDiary(markerId)
            onDeleteComplete()
        }
    }
}
