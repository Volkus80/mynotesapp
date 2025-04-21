package ru.volkus.mynotesproj.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class Note(
    var header: String = "",
    val uuid:UUID = UUID.randomUUID(),
    val timeStamp: LocalDateTime = LocalDateTime.now(),
    val items: MutableList<Item> = mutableListOf()): Parcelable
