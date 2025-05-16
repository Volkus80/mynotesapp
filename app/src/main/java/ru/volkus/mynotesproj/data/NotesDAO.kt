package ru.volkus.mynotesproj.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
    suspend fun getNote(id: UUID): NoteData

    @Transaction
    @Query("SELECT * FROM notes WHERE header LIKE '%' || :filterValue || '%'")
    fun filterNotes(filterValue: String): Flow<MutableList<NoteData>>

    @Query("SELECT * FROM items WHERE parentId = (:id)")
    fun getItems(id: UUID): Flow<MutableList<Item>>

    @Delete(entity = Note::class)
    suspend fun deleteNote(note: Note)

    @Delete(entity = Item::class)
    suspend fun deleteItems(items: List<Item>)

    @Insert(entity = Note::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Insert(entity = Item::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItems(items: List<Item>)

    @Update(entity = Note::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Update(entity = Item::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItems(items: List<Item>)

}