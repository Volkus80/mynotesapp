package ru.volkus.mynotesproj.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.FragmentNoteBinding
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.NoteData
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.coroutines.coroutineContext

private const val TAG = "NoteFragment"
class NoteFragment: Fragment(R.layout.fragment_note) {
    lateinit var noteData: NoteData

    val viewModel by viewModels<NoteDataViewModel> ()

    var _binding: FragmentNoteBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, TAG)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            noteData = requireArguments().getParcelable(NOTE, NoteData::class.java) ?: NoteData()
        } else {
            noteData = requireArguments().getParcelable(NOTE) ?: NoteData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        val adapter = NoteItemsAdapter(noteData.items){addItem()}
        binding.apply {
            val layoutManager = LinearLayoutManager(context)
//            layoutManager.stackFromEnd = true
            rvItems.layoutManager = layoutManager
            rvItems.adapter = adapter
            etTitle.setText(noteData.note.header)
            etTitle.requestFocus()
            tvDateTime.setText(noteData.note.timeStamp.format(DateTimeFormatter.ofPattern("dd MMM yyy")) ?: resources.getText(R.string.default_first_note))

        }



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i(TAG, "onBackPressed started")
                    addNote()
                }

            })

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            addNote()
        }

        binding.btnAddItem.setOnClickListener {
            addItem()
        }

        binding.etTitle.doOnTextChanged{text, _, _, _ ->  noteData.note.header = text.toString()}

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun addNote() {
        Log.i(TAG, "addNote started")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addNote(noteData)
        }
        findNavController().navigate(R.id.exitNote)
    }

    private fun addItem() {
        noteData.items.add(
            Item(itemId = UUID.randomUUID(), parentId = noteData.note.noteId)
        )
        val adapter = NoteItemsAdapter(noteData.items){addItem()}
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager?.scrollToPosition(noteData.items.size - 1)
    }

}