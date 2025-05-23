package ru.volkus.mynotesproj.utils

import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.presentation.NoteItemsAdapter

class NoteItemsAdapterItemAdder: ItemsAdapterUpdater {
    override fun update(adapter: NoteItemsAdapter, items: List<Item>) {
        adapter.items.clear()
        adapter.items.addAll(items)
        adapter.notifyItemInserted(items.size - 1)
    }
}