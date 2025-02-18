package com.taein.travelmap.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taein.travelmap.detailPhotoMarker.Diary

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey val id: String,
    val photo: List<Uri>,
    val date: String,
    val contents: String
)

fun DiaryEntity.asExternalModel() = Diary(
    id = id,
    photo = photo,
    date = date,
    contents = contents
)