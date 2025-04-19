package ru.volkus.mynotesproj.presentation

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.R

class NoteSwipeDeleter(val adapter: NotesListAdapter, val context: Context, val cb: (pos: Int) -> Unit)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.layoutPosition
        val isUserAgree = AlertDialog.Builder(context)
            .setTitle(R.string.delete_note_dialog_title)
            .setMessage(R.string.delete_note_dialog_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                cb(position)
                adapter.notifyItemRemoved(position)
            }
            .setNegativeButton(R.string.no) { _, _ ->
                adapter.notifyItemChanged(position)
            }

        isUserAgree.show()
    }
}