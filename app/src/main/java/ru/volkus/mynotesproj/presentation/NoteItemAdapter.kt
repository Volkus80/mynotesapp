package ru.volkus.mynotesproj.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.volkus.mynotesproj.databinding.LayoutItemBinding
import ru.volkus.mynotesproj.models.Item

class ItemViewHolder(val binding: LayoutItemBinding): RecyclerView.ViewHolder(binding.root) {
    lateinit var item: Item
    fun bind(item: Item) {
        this.item = item
        binding.apply {
            chbDone.isChecked = item.isDone
            text.setText(item.text)
        }
    }
}

class NoteItemsAdapter(val items: MutableList<Item>): RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(inflater, parent, false)
        return  ItemViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

}