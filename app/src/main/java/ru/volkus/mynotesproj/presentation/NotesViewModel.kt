package ru.volkus.mynotesproj.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.data.NotesRepo
import ru.volkus.mynotesproj.models.NoteData

private const val TAG = "NotesViewModel"
class NotesViewModel: ViewModel() {
    private val notesRepo = NotesRepo.getInstance()
    private val _notes = MutableStateFlow<MutableList<NoteData>>(mutableListOf())
    val notes get() = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            notesRepo.getNotes().collect{_notes.value = it}
        }
    }

    private val _filterValue = MutableLiveData("")

    val filterValue: LiveData<String> get() = _filterValue

    fun setFilter(part: String) {
        _filterValue.value = part
    }

    suspend fun filter() {
        notesRepo.filterNotes(_filterValue.value ?: "").collect{_notes.value = it}
    }

    fun removeNote(note: NoteData) {
        notesRepo.deleteNote(note)
    }

}