package ru.volkus.mynotesproj.utils

import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.presentation.NoteItemsAdapter

class NoteItemsAdapterItemDeleter: ItemsAdapterUpdater {
    override fun update(adapter: NoteItemsAdapter,items: List<Item>) {
        var pos = findItemsIndex(adapter)
        if(pos != -1) {
            adapter.items.clear()
            adapter.items.addAll(items)
            adapter.notifyItemRemoved(pos)
            adapter.notifyItemRangeChanged(pos, items.size - 1)
        }
    }
}