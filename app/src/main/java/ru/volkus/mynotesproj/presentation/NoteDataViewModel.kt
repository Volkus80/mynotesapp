package ru.volkus.mynotesproj.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.data.NotesRepo
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.NoteData
import ru.volkus.mynotesproj.utils.ItemsAdapterUpdaterType
import java.util.UUID

private const val TAG = "NoteDataViewModel"

class NoteDataViewModel(private val noteId: UUID): ViewModel() {
    private val notesRepo = NotesRepo.getInstance()

    private val _noteData = MutableStateFlow<NoteData?>(null)
    val noteData get() = _noteData.asStateFlow()
//    private val _note = MutableStateFlow<Note?>(null)
//    val note get() = _note.asStateFlow()
//
//    private val _items = MutableStateFlow<MutableList<Item>?>(null)
//    val items get() = _items.asStateFlow()

    private var baseItems = listOf<Item>()

    var updaterType = ItemsAdapterUpdaterType.CREATE



    init {
        Log.i(TAG, "initiating")
        viewModelScope.launch {
            notesRepo.getNote(noteId).collect{
                Log.i(TAG, "noteData = $it")
                it?.let {
                    _noteData.value = it
                    baseItems = it.items
                }
            }
        }
    }


    fun addItem(item: Item) {
        Log.i(TAG, "viewModel addItem started")
        _noteData?.update { nd ->
            nd?.let {
                val newItems = it.items.toMutableList()
                newItems.add(item)
                it.copy(items = newItems)
            }
        }
    }

    fun removeItem(pos: UUID) {
        Log.i(TAG, "removeItemStarted")
        _noteData.value?.let {nd ->
            val changedItems = nd.items.filter { it.itemId != pos }.toMutableList()
            _noteData.update { nd.copy(items = changedItems) }
        }
    }

    fun updateItem(item: Item) {
        Log.i(TAG, "vm updateItem started $item noteData = ${_noteData.value}")
        _noteData.value?.let { nd ->
            Log.i(TAG, "updating noteData")
            val newNd = nd.copy(items = nd.items.toMutableList())

            newNd.items.forEach { i ->
                if(i.itemId == item.itemId) {
                    i.isDone = item.isDone
                    i.text = item.text
                }
            }

            _noteData.update { newNd }
        }
    }

    fun setTitle(text: String) {
        _noteData.value?.let { nd ->
            if (text != nd.note.header) {
                updaterType = ItemsAdapterUpdaterType.UPDATE
                _noteData.update {
                    nd.copy(note = nd.note.copy(header = text))
                }
            }
        }
    }

    suspend fun saveNoteDataChanges() {
        Log.i(TAG, "saveNoteDataChanges started")
        _noteData.value?.let {
            Log.i(TAG, "newNoteData = $it")
            notesRepo.updateNote(it.note, baseItems, it.items)
        }
    }

    private fun deleteEmptyItems(items: MutableList<Item>): List<Item> {
        return items.toMutableList().filter { it.text.isNotBlank() }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            saveNoteDataChanges()
        }
    }
}