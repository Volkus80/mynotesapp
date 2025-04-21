package ru.volkus.mynotesproj.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.FragmentNotesBinding

private const val TAG = "NotesFragment"
class NotesFragment: Fragment(R.layout.fragment_notes) {
    private var _binding: FragmentNotesBinding? = null

    private val binding get() = checkNotNull(_binding)

    private val viewModel: NotesViewModel by viewModels()

    private lateinit var adapter: NotesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "NotesFragment started")
        adapter = NotesListAdapter(viewModel.filteredNotes.value?.toMutableList() ?: mutableListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.rvNotesList.layoutManager = LinearLayoutManager(context)

        binding.rvNotesList.adapter = adapter

        binding.etFind.setText(viewModel.filterValue.value)

        val noteSwipeDeleter = NoteSwipeDeleter(adapter, requireContext()){ pos -> viewModel.removeNote(pos)}

        val helper = ItemTouchHelper(noteSwipeDeleter)
        helper.attachToRecyclerView(binding.rvNotesList)


        viewModel.filteredNotes.observe(viewLifecycleOwner) {
            val filteredNotes = it.toMutableList()
            adapter.notes.clear()
            adapter.notes.addAll(filteredNotes)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.etFind.doOnTextChanged{text, _, _, _ ->
            viewModel.setFilter("$text")
            viewModel.setFiltered()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}