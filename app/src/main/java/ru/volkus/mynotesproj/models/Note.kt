package ru.volkus.mynotesproj.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
@Entity(tableName = "notes")
data class Note(
    var header: String = "",
    @PrimaryKey val noteId:UUID = UUID.randomUUID(),
    val timeStamp: LocalDateTime = LocalDateTime.now(),
    ): Parcelable
