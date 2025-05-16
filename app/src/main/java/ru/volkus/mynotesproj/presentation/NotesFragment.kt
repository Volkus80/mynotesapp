package ru.volkus.mynotesproj.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.FragmentNotesBinding
import ru.volkus.mynotesproj.models.NoteData
import ru.volkus.mynotesproj.models.NoteState

private const val TAG = "NotesFragment"
const val NOTE_KEY = "note"
const val EDIT_TYPE_KEY = "editTypeKey"
class NotesFragment: Fragment(R.layout.fragment_notes) {
    private var _binding: FragmentNotesBinding? = null

    private val binding get() = checkNotNull(_binding)

    private val viewModel: NotesViewModel by viewModels()

    private var adapter: NotesListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "NotesFragment started")
        adapter = NotesListAdapter(
            viewModel.notes.value,
        ) { note: NoteData -> goToNote(note, NoteState.EDIT) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.rvNotesList.layoutManager = LinearLayoutManager(context)

        binding.rvNotesList.adapter = adapter

        val noteSwipeDeleter = NoteSwipeDeleter(adapter, requireContext()){ pos ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModel.removeNote(pos)
            }
        }

        val helper = ItemTouchHelper(noteSwipeDeleter)
        helper.attachToRecyclerView(binding.rvNotesList)

        binding.etFind.setText(viewModel.filterValue.value)

        viewModel.filterValue.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(500)
                viewModel.filter()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collect {
                    updateNotesAdapter(it)
                }
            }
        }

        binding.btnAddNote.setOnClickListener {
            val newNote = NoteData()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.addNote(newNote)
                goToNote(newNote, NoteState.NEW)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")

        binding.etFind.doOnTextChanged { text, start, before, count ->
            viewModel.setFilter("$text")
            Log.i(TAG, "textChangedParams text = $text start = $start before = $before count = $count")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToNote(note: NoteData, noteState: NoteState) {
        findNavController().navigate(NotesFragmentDirections.goToNote(note.note.noteId, noteState))
//        findNavController().navigate(R.id.goToNote, bundleOf(NOTE_KEY to note))
    }

    private fun updateNotesAdapter(notesList: MutableList<NoteData>) {
        adapter?.let {
            it.notes.clear()
            it.notes.addAll(notesList)
            it.notifyDataSetChanged()
            Log.i(TAG, "notesList = $notesList")
        }
    }
}
