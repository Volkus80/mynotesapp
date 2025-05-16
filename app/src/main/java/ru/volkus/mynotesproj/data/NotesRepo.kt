package ru.volkus.mynotesproj.data

import android.content.Context
import androidx.room.Room
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.Note
import ru.volkus.mynotesproj.models.NoteData
import java.util.UUID

private const val DbName = "notes-db"
class NotesRepo private constructor(context: Context){
    private val db = Room.databaseBuilder(context.applicationContext, NotesDB::class.java, DbName).build()
    companion object {
        private var INSTANCE: NotesRepo? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = NotesRepo(context)
            }
        }

        fun getInstance() = checkNotNull(INSTANCE) {
            "NotesRepo is not initialized"
        }
    }

    fun getNotes() = db.notesDao().getNotes()
    suspend fun getNote(id:UUID) = db.notesDao().getNote(id)
    fun getItems(id: UUID) = db.notesDao().getItems(id)
    suspend fun addNote(note: NoteData) {
        db.notesDao().addNote(note.note)
        db.notesDao().addItems(note.items)
    }

    suspend fun addItem(item: Item) {
        db.notesDao().addItems(listOf(item))
    }

    suspend fun updateNote(note: Note, oldItems: List<Item>, actualItems: List<Item>) {
        val itemsToDelete = oldItems - actualItems
        val newItems = actualItems - oldItems
        val itemsForUpdate = actualItems subtract newItems

        db.notesDao().updateNote(note = note)
        db.notesDao().updateItems(itemsForUpdate.toList())
        db.notesDao().deleteItems(itemsToDelete)
        db.notesDao().addItems(newItems)
    }

    suspend fun deleteNote(note: NoteData) {
        db.notesDao().deleteNote(note.note)
        db.notesDao().deleteItems(note.items)
    }

    suspend fun deleteItems(items: List<Item>) {
        db.notesDao().deleteItems(items)
    }

    fun filterNotes(filterValue: String) = db.notesDao().filterNotes(filterValue)

}