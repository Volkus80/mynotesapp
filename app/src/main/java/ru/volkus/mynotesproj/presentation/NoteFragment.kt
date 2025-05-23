package ru.volkus.mynotesproj.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import ru.volkus.mynotesproj.utils.ItemsAdapterUpdaterSupplier
import ru.volkus.mynotesproj.utils.ItemsAdapterUpdaterType
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val TAG = "NoteFragment"
class NoteFragment: Fragment(R.layout.fragment_note) {

    private val arguments: NoteFragmentArgs by navArgs()

    private val viewModel: NoteDataViewModel by viewModels{NoteDataViewModelFactory(arguments.noteId)}

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var adapter: NoteItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, TAG)
        Log.i(TAG, "items = ${viewModel.noteData.value?.items}")
        adapter = NoteItemsAdapter(
            viewModel.noteData.value?.items ?: mutableListOf(),
            { addItem() },
            { item -> updateItem(item) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        binding.apply {
            val layoutManager = LinearLayoutManager(context)
            rvItems.layoutManager = layoutManager
            rvItems.adapter = adapter

            viewModel.noteData.value?.let {
                etTitle.setText(it.note.header)
                tvDateTime.text = it.note.timeStamp.format(DateTimeFormatter.ofPattern("dd MMM yyy")) ?: resources.getText(R.string.default_first_note)
            }


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

        binding.apply {
            header.apply {
                setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.addNoteItem -> {
                            addItem()
                            true
                        }

                        R.id.removeNoteItem -> {
                            deleteItem(adapter.activePosition)
                            true
                        }

                        else -> false
                    }
                }

                setNavigationOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            etTitle.apply {
                doOnTextChanged{text, _, _, _ ->
                    viewModel.setTitle(text.toString())
                    binding.etTitle.setSelection(binding.etTitle.text.length)
                }

                setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus) {
                        adapter.activePosition = null
                        showInput(this)
                    }
                }
                requestFocus()
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noteData.collect { nd ->
                    Log.i(TAG, "noteData note changed")
                    nd?.let {
                        Log.i(TAG, "noteData note updated")
                        updateHeader(it.note.header)
                        updateItems(it.items)
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        Log.i(TAG, "onDestroyView started")
        super.onDestroyView()
        _binding = null
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveNoteDataChanges()
        }
    }


    private fun addNote() {
        Log.i(TAG, "addNote started")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveNoteDataChanges()
        }
        findNavController().navigate(R.id.exitNote)
    }

    private fun addItem() {
        viewModel.updaterType = ItemsAdapterUpdaterType.ADD
        viewModel.noteData.value?.let {
            val item = Item(parentId = it.note.noteId)
            viewModel.addItem(item)
        }
    }

    private fun deleteItem(pos: UUID?) {
        if (pos == null) return
        viewModel.updaterType = ItemsAdapterUpdaterType.DELETE
        viewModel.removeItem(pos)
    }

    private fun updateHeader(text: String?) {
        binding.etTitle.setText(text)
    }

    private fun updateItems(items: List<Item>?) {
        Log.i(TAG, "updateItems started")
        items?.let {
            val itemsAdapterUpdater = ItemsAdapterUpdaterSupplier(viewModel.updaterType).get()
            Log.i(TAG, "itemsAdapter = ${itemsAdapterUpdater.javaClass.simpleName}")
            itemsAdapterUpdater.update(adapter, it)
            if(viewModel.updaterType == ItemsAdapterUpdaterType.ADD) {
                binding.rvItems.layoutManager?.scrollToPosition(it.size - 1)
            }
        }
    }

    private fun updateItem(item: Item) {
        Log.i(TAG, "updateItem started")
        viewModel.updaterType = ItemsAdapterUpdaterType.UPDATE
        viewModel.updateItem(item)
    }

    private fun showInput(view: View) {
        Log.i(TAG, "showInput started")
//        val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

}