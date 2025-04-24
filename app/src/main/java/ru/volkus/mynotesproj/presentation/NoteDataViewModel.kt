package ru.volkus.mynotesproj.presentation

import androidx.lifecycle.ViewModel
import ru.volkus.mynotesproj.data.NotesRepo
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.NoteData

class NoteDataViewModel: ViewModel() {
    private val notesRepo = NotesRepo.getInstance()

    fun addNote(note: NoteData) {
        val newItems = deleteEmptyItems(note.items)
        if (note.note.header.isNotBlank() || newItems.isNotEmpty()) {
            notesRepo.addNote(note)
        }
    }

    private fun deleteEmptyItems(items: MutableList<Item>): List<Item> {
        val newItems = items.toMutableList().filter { it.text.isNotBlank() }
        return newItems
    }
}