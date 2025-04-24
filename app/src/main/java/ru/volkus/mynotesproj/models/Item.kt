package ru.volkus.mynotesproj.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "items")
data class Item(
    @PrimaryKey val itemId: UUID,
    val parentId: UUID,
    var text: String = "",
    var isDone: Boolean = false): Parcelable
