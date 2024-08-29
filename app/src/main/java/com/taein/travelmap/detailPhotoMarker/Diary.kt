package com.taein.travelmap.detailPhotoMarker

import com.taein.travelmap.model.DiaryEntity
import java.time.LocalDateTime

data class Diary(
    val id: String,
    val photo: String,
    val date: LocalDateTime,
    val contents: String
)

fun Diary.asEntity() = DiaryEntity(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)