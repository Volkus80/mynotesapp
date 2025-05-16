package ru.volkus.mynotesproj.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

class NoteDataViewModelFactory(private val noteId: UUID): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteDataViewModel(noteId) as T
    }
}