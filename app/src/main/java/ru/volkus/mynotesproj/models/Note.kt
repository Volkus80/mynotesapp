package ru.volkus.mynotesproj.models

import java.time.LocalDateTime
import java.util.UUID

data class Note(
    var header: String = "",
    val uuid:UUID = UUID.randomUUID(),
    val timeStamp: LocalDateTime = LocalDateTime.now(),
    val items: MutableList<Item> = mutableListOf())
