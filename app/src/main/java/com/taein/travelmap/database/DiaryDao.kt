package com.taein.travelmap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taein.travelmap.model.DiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(photoMarker: DiaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaries(photoMarkers: List<DiaryEntity>)

    @Query("SELECT * FROM diaries")
    fun getAllDiaries(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diaries WHERE id = :id")
    fun getDiary(id: String): Flow<DiaryEntity>
}
