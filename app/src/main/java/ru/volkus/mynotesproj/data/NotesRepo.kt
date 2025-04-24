package ru.volkus.mynotesproj.data

import android.content.Context
import androidx.room.Room
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
    fun getNote(id:UUID) = db.notesDao().getNote(id)
    fun getItems(id: UUID) = db.notesDao().getItems(id)
    fun addNote(note: NoteData) {
        db.notesDao().addNote(note.note)
        db.notesDao().addItems(note.items)
    }

    fun deleteNote(note: NoteData) {
        db.notesDao().deleteNote(note.note)
        db.notesDao().deleteItems(note.items)
    }

    fun filterNotes(filterValue: String) = db.notesDao().filterNotes(filterValue)

}