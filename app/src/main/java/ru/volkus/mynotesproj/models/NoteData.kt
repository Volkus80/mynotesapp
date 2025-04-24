package ru.volkus.mynotesproj.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteData(
    @Embedded val note: Note = Note(),
    @Relation(
        parentColumn = "noteId",
        entityColumn = "parentId"
    )
    val items: MutableList<Item> = mutableListOf()
): Parcelable