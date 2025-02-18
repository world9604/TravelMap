package com.taein.travelmap.repository.diary

import com.taein.travelmap.detailPhotoMarker.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    suspend fun addDiary(diary: Diary)

    suspend fun addDiaries(diaries: List<Diary>)

    fun observeAll(): Flow<List<Diary?>>

    fun observe(id: String): Flow<Diary?>
}