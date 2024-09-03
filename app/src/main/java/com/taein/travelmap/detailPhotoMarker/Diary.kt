package com.taein.travelmap.detailPhotoMarker

import com.taein.travelmap.model.DiaryEntity

data class Diary(
    val id: String,
    val photo: List<String>,
    val date: String,
    val contents: String
)

fun Diary.asEntity() = DiaryEntity(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)