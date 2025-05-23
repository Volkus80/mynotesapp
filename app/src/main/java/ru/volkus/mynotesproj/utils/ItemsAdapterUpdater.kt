package ru.volkus.mynotesproj.utils

import ru.volkus.mynotesproj.models.Item
import ru.volkus.mynotesproj.presentation.NoteItemsAdapter

interface ItemsAdapterUpdater {
    fun update(adapter: NoteItemsAdapter, items: List<Item>)

    fun findItemsIndex(adapter: NoteItemsAdapter): Int {
        var index = -1

        adapter.items.forEachIndexed{i, item ->
            if(item.itemId == adapter.activePosition) index = i
        }
        return  index
    }
}