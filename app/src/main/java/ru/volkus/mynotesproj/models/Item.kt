package ru.volkus.mynotesproj.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Item(
    val uuid: UUID,
    val parentUuid: UUID,
    var text: String = "",
    var isDone: Boolean = false): Parcelable
