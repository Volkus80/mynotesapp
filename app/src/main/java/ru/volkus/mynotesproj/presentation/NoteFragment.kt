package ru.volkus.mynotesproj.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.FragmentNoteBinding
import ru.volkus.mynotesproj.models.Item
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val TAG = "NoteFragment"
class NoteFragment: Fragment(R.layout.fragment_note) {
//    private lateinit var noteData: NoteData
    val arguments: NoteFragmentArgs by navArgs()

    private val viewModel: NoteDataViewModel by viewModels{NoteDataViewModelFactory(arguments.noteId)}

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var adapter: NoteItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, TAG)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            noteData = requireArguments().getParcelable(NOTE_KEY, NoteData::class.java) ?: NoteData()
//        } else {
//            noteData = requireArguments().getParcelable(NOTE_KEY) ?: NoteData()
//        }

//        adapter = NoteItemsAdapter(noteData.items){addItem()}
        adapter = NoteItemsAdapter(viewModel.items.value){addItem()}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        binding.apply {
            val layoutManager = LinearLayoutManager(context)
//            layoutManager.stackFromEnd = true
            rvItems.layoutManager = layoutManager
            rvItems.adapter = adapter

            viewModel.note.value?.let {
                etTitle.setText(it.header)
                tvDateTime.setText(it.timeStamp.format(DateTimeFormatter.ofPattern("dd MMM yyy")) ?: resources.getText(R.string.default_first_note))
            }

            etTitle.requestFocus()
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

        binding.header.apply {
            setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.addNoteItem -> {
                        addItem()
                        true
                    }

                    R.id.removeNoteItem -> {
                        deleteItem(adapter.activePosition)
//                        adapter.notifyDataSetChanged()
                        true
                    }

                    else -> false
                }
            }

            setNavigationOnClickListener { addNote() }
        }

        binding.etTitle.doOnTextChanged{text, _, _, _ ->
            viewModel.setTitle(text.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.note.collect { noteData ->

                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveNoteDataChanges()
        }
    }


    private fun addNote() {
        Log.i(TAG, "addNote started")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addNote(noteData)
        }
        findNavController().navigate(R.id.exitNote)
    }

    private fun addItem() {
        val item = Item(parentId = viewModel.note.value.noteId)
        viewModel.addItem(item)
        val position = viewModel.items.value.size - 1
        adapter.notifyItemInserted(position)
        binding.rvItems.layoutManager?.scrollToPosition(position)
    }

    private fun deleteItem(pos: Int) {
        viewModel.removeItem(pos)
        adapter.notifyItemRemoved(pos)
        adapter.notifyItemRangeChanged(pos, viewModel.items.value.size - 1)
    }

}