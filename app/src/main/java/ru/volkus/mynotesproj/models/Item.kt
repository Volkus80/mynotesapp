package ru.volkus.mynotesproj.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "items")
data class Item(
    @PrimaryKey val itemId: UUID = UUID.randomUUID(),
    val parentId: UUID,
    var text: String = "",
    var isDone: Boolean = false): Parcelable
{
        override fun equals(other: Any?): Boolean {
            if(other !is Item) return  false

            return (
                    other.itemId == itemId
                            && other.isDone == isDone
                            && other.text == text
                            && other.parentId == parentId
                    )
        }

}

