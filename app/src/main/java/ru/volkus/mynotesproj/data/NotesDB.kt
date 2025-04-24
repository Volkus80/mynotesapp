package ru.volkus.mynotesproj.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.Note

@Database(entities = [Note::class, Item::class], version = 1)
@TypeConverters(NotesTypeConverters::class)
abstract class NotesDB: RoomDatabase() {
    abstract fun notesDao(): NotesDAO
}