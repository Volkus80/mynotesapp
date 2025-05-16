package ru.volkus.mynotesproj.presentation

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.databinding.LayoutItemBinding
import ru.volkus.mynotesproj.models.Item

private const val TAG = "NoteItemAdapter"
class ItemViewHolder(
    val binding: LayoutItemBinding,
    val posGetter: (pos: Int) -> Unit,
    val itemCreator: () -> Unit = {}
): RecyclerView.ViewHolder(binding.root) {
    lateinit var mainItem: Item
    fun bind(item: Item, isFocused: Boolean = false) {
        Log.i(TAG, "bind is started")
        mainItem = item
        Log.i(TAG, "mainItem = $mainItem")
        binding.apply {
            chbDone.isChecked = item.isDone
            chbDone.setOnCheckedChangeListener { _, isChecked ->
                item.isDone = isChecked
                text.requestFocus()
            }

            with(text) {
                setText(item.text)

                doOnTextChanged { value, _, _, _ ->
                    item.text = value as String
                }

                if(isFocused) {
                    requestFocus()
                    posGetter(layoutPosition)
                }

                setOnFocusChangeListener { _, hasFocus ->
                    Log.i(TAG, "focus changed itemId = ${mainItem.itemId}")
                    if(hasFocus) {
                        posGetter(layoutPosition)
                    }
                }

            }
        }
    }
}

class NoteItemsAdapter(val items: List<Item>, val cb: () -> Unit = {}): RecyclerView.Adapter<ItemViewHolder>() {

    var activePosition = items.size - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(inflater, parent, false)
        val posGetter = { pos: Int ->
            Log.i(TAG, "cb called uuid = $pos")
            activePosition = pos
        }


        return ItemViewHolder(binding, posGetter, cb)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position == items.size - 1)
    }

}