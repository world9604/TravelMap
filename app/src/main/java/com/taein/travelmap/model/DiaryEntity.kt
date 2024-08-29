package com.taein.travelmap.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taein.travelmap.detailPhotoMarker.Diary
import java.time.LocalDateTime

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey val id: String,
    val photo: String,
    val date: LocalDateTime,
    val contents: String
)

fun DiaryEntity.asExternalModel() = Diary(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)