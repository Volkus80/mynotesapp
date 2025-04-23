package ru.volkus.mynotesproj.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.FragmentNoteBinding
import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.models.Note
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val TAG = "NoteFragment"
class NoteFragment: Fragment(R.layout.fragment_note) {
    var note: Note? = null

    var _binding: FragmentNoteBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            note = requireArguments().getParcelable(NOTE, Note::class.java)
        } else {
            note = requireArguments().getParcelable(NOTE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)

        val adapter = NoteItemsAdapter(note?.items ?: mutableListOf())
        binding.apply {
            rvItems.layoutManager = LinearLayoutManager(context)
            rvItems.adapter = adapter
            etTitle.setText(note?.header ?: resources.getText(R.string.default_first_note))
            etTitle.requestFocus()
            tvDateTime.setText(note?.timeStamp?.format(DateTimeFormatter.ofPattern("dd MMM yyy")) ?: resources.getText(R.string.default_first_note))
        }

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAddItem.setOnClickListener {
            note?.let {
                note!!.items.add(
                    Item(uuid = UUID.randomUUID(), parentUuid = note!!.uuid)
                )
            }

            val adapter = NoteItemsAdapter(note!!.items)
            binding.rvItems.adapter = adapter

        }

        binding.etTitle.doOnTextChanged{text, _, _, _ ->  note!!.header = text.toString()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}