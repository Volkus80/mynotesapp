package ru.volkus.mynotesproj.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.data.NotesRepo
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.Note
import java.util.UUID

class NoteDataViewModel(private val noteId: UUID): ViewModel() {
    private val notesRepo = NotesRepo.getInstance()

    private val _note = MutableStateFlow<Note?>(null)

    val note get() = _note.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(mutableListOf())
    val items get() = _items.asStateFlow()

    private var baseItems = listOf<Item>()



    init {
        viewModelScope.launch {
            val noteData = notesRepo.getNote(noteId)
            _note.value = noteData.note
            _items.value = noteData.items
            baseItems = noteData.items
        }
    }


    fun addItem(item: Item) {
        _items.update { it + item }
//        viewModelScope.launch {
//            notesRepo.addItem(item)
//        }
    }

    fun removeItem(pos: Int) {
        _items.update { it - it[pos] }
//        viewModelScope.launch {
//            notesRepo.deleteItems(listOf(item))
//        }
    }

    fun setTitle(text: String) {
        _note.value?.let { note ->
            if (text != note.header) {
                _note.update {
                    note.copy(header = text)
                }
            }
        }
    }

    suspend fun saveNoteDataChanges() {
        _note.value?.let {
            notesRepo.updateNote(it, baseItems, _items.value)
        }
    }

    private fun deleteEmptyItems(items: MutableList<Item>): List<Item> {
        val newItems = items.toMutableList().filter { it.text.isNotBlank() }
        return newItems
    }
}