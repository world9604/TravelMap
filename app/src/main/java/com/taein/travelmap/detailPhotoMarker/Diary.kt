package com.taein.travelmap.detailPhotoMarker

import android.net.Uri
import com.taein.travelmap.model.DiaryEntity

data class Diary(
    val id: String,
    val photo: List<Uri>,
    val date: String,
    val contents: String
)

fun Diary.asEntity() = DiaryEntity(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)