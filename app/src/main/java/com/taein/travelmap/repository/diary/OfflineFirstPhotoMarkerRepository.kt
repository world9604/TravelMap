package com.taein.travelmap.repository.diary

import com.taein.travelmap.database.DiaryDao
import com.taein.travelmap.detailPhotoMarker.Diary
import com.taein.travelmap.detailPhotoMarker.asEntity
import com.taein.travelmap.model.DiaryEntity
import com.taein.travelmap.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstDiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override suspend fun addDiary(diary: Diary) {
        diaryDao.insertDiary(diary.asEntity())
    }

    override suspend fun addDiaries(diaries: List<Diary>) {
        diaryDao.insertDiaries(diaries.map(Diary::asEntity))
    }

    override fun observeAll(): Flow<List<Diary>> =
        diaryDao.getAllDiaries()
            .map { it.map(DiaryEntity::asExternalModel) }
}