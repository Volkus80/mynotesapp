package ru.volkus.mynotesproj.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.volkus.mynotesproj.models.Note

private const val TAG = "NotesViewModel"
class NotesViewModel: ViewModel() {
    private val notes = mutableListOf<Note>()

    init {
        for (index in 1..10) {
            val note = Note("Заголовок$index")
            notes += note
        }
    }

    private val _filterValue = MutableLiveData("")

    val filterValue: LiveData<String> get() = _filterValue

    fun setFilter(part: String) {
        _filterValue.value = part
    }

    private val _filteredNotes = MutableLiveData(notes)
    val filteredNotes: LiveData<MutableList<Note>> get() = _filteredNotes
    fun setFiltered() {
        Log.i(TAG, "filter started")
        Log.i(TAG, _filteredNotes.value.toString())
        _filteredNotes.value = filter()
        Log.i(TAG, "filter result = ${_filteredNotes.value}")
    }

    private fun filter() = notes.filter { it.header.lowercase().contains(_filterValue.value!!.lowercase().toRegex()) } as MutableList

    fun removeNote(note: Note) {
        Log.i(TAG, "removeNote started note = $note")
        Log.i(TAG, notes.toString())
        notes.remove(note)
        _filteredNotes.value = filter()
        Log.i(TAG, notes.toString())
    }

    fun addNote(note: Note) {
        Log.i(TAG, "addNote started")
        notes.add(note)
        Log.i(TAG, "notes $notes")
        _filteredNotes.value = filter()
        Log.i(TAG, "_filteredNotes.value ${_filteredNotes.value}")
    }
}