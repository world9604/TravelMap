package com.taein.travelmap.detailPhotoMarker

import com.taein.travelmap.model.DiaryEntity
import java.util.Calendar

data class Diary(
    val id: String,
    val photo: String,
    val date: Calendar,
    val contents: String
)

fun Diary.asEntity() = DiaryEntity(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)