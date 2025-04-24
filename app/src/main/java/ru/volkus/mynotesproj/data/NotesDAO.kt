package ru.volkus.mynotesproj.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.Note
import ru.volkus.mynotesproj.models.NoteData
import java.util.UUID

@Dao
interface NotesDAO {
    @Transaction
    @Query("SELECT * FROM notes")
    fun getNotes(): Flow<MutableList<NoteData>>

    @Transaction
    @Query("SELECT * FROM notes WHERE noteId = :id")
    fun getNote(id: UUID): Flow<NoteData>

    @Transaction
    @Query("SELECT * FROM notes WHERE header LIKE '%' || :filterValue || '%'")
    fun filterNotes(filterValue: String): Flow<MutableList<NoteData>>

    @Query("SELECT * FROM items WHERE parentId = (:id)")
    fun getItems(id: UUID): Flow<MutableList<Item>>

    @Delete(entity = Note::class)
    fun deleteNote(note: Note)

    @Delete(entity = Item::class)
    fun deleteItems(items: List<Item>)

    @Insert(entity = Note::class, onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note)

    @Insert(entity = Item::class, onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<Item>)

}