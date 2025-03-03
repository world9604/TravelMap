package com.taein.travelmap.database.util

import android.net.Uri
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UriTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun calendarToString(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    @TypeConverter
    fun stringToCalendar(dateTimeString: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = dateFormat.parse(dateTimeString) ?: throw IllegalArgumentException("Invalid date format")

        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    @TypeConverter
    fun listStringToString(listString: List<String>): String
        = listString.joinToString(",")

    @TypeConverter
    fun stringToListString(string: String): List<String>
        = string.split(",")

    @TypeConverter
    fun fromUriList(uriList: List<Uri>): String {
        return uriList.joinToString(separator = ",") { it.toString() }
    }

    @TypeConverter
    fun toUriList(data: String): List<Uri> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { Uri.parse(it) }
    }
}
