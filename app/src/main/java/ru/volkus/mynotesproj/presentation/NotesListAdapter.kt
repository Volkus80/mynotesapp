package ru.volkus.mynotesproj.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.R
import ru.volkus.mynotesproj.databinding.LayoutNoteListItemBinding
import ru.volkus.mynotesproj.models.Note
import java.time.format.DateTimeFormatter

private const val TAG = "NoteViewHolder"

class NoteViewHolder(val binding: LayoutNoteListItemBinding, val cb: (index: Int) -> Unit = {}): RecyclerView.ViewHolder(binding.root) {
    var note:Note? = null
    fun bind(note: Note) {
        this.note = note
        with(binding) {
            tvTitle.text = note.header.ifBlank {
                ContextCompat.getString(binding.root.context, R.string.default_title)
            }
            tvDescription.text = if(note.items.isNotEmpty() && note.items[0].text.isNotBlank()) {
                note.items[0].text
            } else ContextCompat.getString(binding.root.context, R.string.default_first_note)
            tvDateCreation.text = note.timeStamp.format(DateTimeFormatter.ofPattern("dd MMM yyy"))
        }
    }
}

class NotesListAdapter(val notes: MutableList<Note>, val cb: (index: Int) -> Unit = {}): RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutNoteListItemBinding.inflate(inflater, parent, false)
        return  NoteViewHolder(binding, cb)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.binding.note.setOnClickListener{
            Log.i(TAG, "note clicked")
            Log.i(TAG, "position $position")
            holder.cb(position)
            notifyDataSetChanged()
        }
    }

}