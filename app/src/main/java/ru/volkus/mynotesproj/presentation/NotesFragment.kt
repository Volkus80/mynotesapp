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

private const val TAG = "NotesFragment"
const val NOTE = "note"
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
        ) { note: NoteData -> goToNote(note) }
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
                    adapter?.notes?.clear()
                    adapter?.notes?.addAll(it)
                    adapter?.notifyDataSetChanged()
                    Log.i(TAG, "notesList = $it")
                }
            }
        }

        binding.btnAddNote.setOnClickListener {
            val newNote = NoteData()
            goToNote(newNote)
        }
    }


    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        binding.etFind.doOnTextChanged{text, _, _, _ ->
            viewModel.setFilter("$text")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToNote(note: NoteData) {
        findNavController().navigate(R.id.goToNote, bundleOf(NOTE to note))
    }
}