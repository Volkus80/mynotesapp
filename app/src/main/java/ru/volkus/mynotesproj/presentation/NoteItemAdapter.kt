package ru.volkus.mynotesproj.presentation

import android.text.Editable
import android.text.InputType
import android.text.method.KeyListener
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.databinding.LayoutItemBinding
import ru.volkus.mynotesproj.models.Item

private const val TAG = "NoteItemAdapter"
class ItemViewHolder(val binding: LayoutItemBinding, val cb: () -> Unit): RecyclerView.ViewHolder(binding.root) {
    lateinit var item: Item
    fun bind(item: Item, isFocused: Boolean = false) {
        this.item = item
        binding.apply {
            chbDone.isChecked = item.isDone
            chbDone.setOnCheckedChangeListener { _, isChecked ->  item.isDone = isChecked}
            text.setText(item.text)
            text.doOnTextChanged { text, _, _, _ ->  item.text = text.toString()}
            if(isFocused) {
                binding.text.requestFocus()
            }

            text.setOnKeyListener { v, keyCode, event ->
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    cb()
                }
                true
            }

        }
    }
}

class NoteItemsAdapter(val items: MutableList<Item>, val cb: () -> Unit = {}): RecyclerView.Adapter<ItemViewHolder>() {
    private val TAG = "NoteItemsAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(inflater, parent, false)
        return  ItemViewHolder(binding, cb)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position == items.size - 1)
    }

}