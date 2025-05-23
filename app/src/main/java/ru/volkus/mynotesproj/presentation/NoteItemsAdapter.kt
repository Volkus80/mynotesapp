package ru.volkus.mynotesproj.presentation

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.textclassifier.TextClassifier
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.databinding.LayoutItemBinding
import ru.volkus.mynotesproj.models.Item
import java.util.UUID

private const val TAG = "NoteItemAdapter"
class ItemViewHolder(
    val binding: LayoutItemBinding,
    val posGetter: (pos: UUID?) -> Unit,
    val itemCreator: () -> Unit = {},
    val itemChanger: (item: Item) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    lateinit var mainItem: Item
    fun bind(item: Item, isFocused: Boolean = false) {
        Log.i(TAG, "bind is started")
        mainItem = item
        Log.i(TAG, "mainItem = $mainItem")
        binding.apply {
            chbDone.isChecked = mainItem.isDone
            chbDone.setOnCheckedChangeListener { _, isChecked ->
                mainItem.isDone = isChecked
                itemChanger(mainItem)
                Log.i(TAG, "Flag = ${text.paintFlags}")

                if(isChecked) {
                   doChecked(text)
                } else {
                    doUnChecked(text)
                }
            }

            with(text) {
                if(mainItem.isDone) {
                    doChecked(this)
                }

                setText(mainItem.text)

                doOnTextChanged { value, _, _, _ ->
                    mainItem.text = value.toString()
                    itemChanger(mainItem)
                }

                if(isFocused) {
                    requestFocus()
                    posGetter(mainItem.itemId)
                }

                setOnFocusChangeListener { _, hasFocus ->
                    Log.i(TAG, "focus changed itemId = ${mainItem.itemId}")
                    if(hasFocus) {
                        Log.i(TAG, "active item = ${mainItem.itemId}")
                        posGetter(mainItem.itemId)
                        requestFocus()
                        setSelection(text.length)
                    }
                }
            }
        }
    }

    private fun doChecked(text: EditText) {
        text.paintFlags = text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        text.isFocusable = false
    }

    private fun doUnChecked(text: EditText) {
        text.paintFlags = text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        text.isFocusableInTouchMode = true
        text.requestFocus()
    }

}

class NoteItemsAdapter(val items: MutableList<Item>,
                       val posAdder: () -> Unit = {},
                       val posChanger: (item: Item) -> Unit = {}
): RecyclerView.Adapter<ItemViewHolder>() {

    var activePosition: UUID? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(inflater, parent, false)
        val posGetter = { id: UUID? ->
            Log.i(TAG, "cb called uuid = $id")
            activePosition = id
        }


        return ItemViewHolder(binding, posGetter, posAdder, posChanger)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, item.itemId == activePosition || position == items.size - 1)
    }

}