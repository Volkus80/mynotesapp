package ru.volkus.mynotesproj.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.volkus.mynotesproj.models.Note

private const val TAG = "NotesViewModel"
class NotesViewModel: ViewModel() {
    val notes = mutableListOf<Note>()

    init {
        for (index in 1..10) {
            val note = Note("Заголовок$index")
            notes += note
        }
    }

    fun removeNote(index: Int) {
        Log.i(TAG, "removeNote started index = $index")
        Log.i(TAG, notes.toString())
        notes.removeAt(index)
        Log.i(TAG, notes.toString())
    }
}