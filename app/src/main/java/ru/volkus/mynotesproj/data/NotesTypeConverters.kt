package ru.volkus.mynotesproj.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class NotesTypeConverters {
    @TypeConverter
    fun fromDate(date: LocalDateTime): Long {
        return  date.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun toDate(epochSeconds: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
    }
}