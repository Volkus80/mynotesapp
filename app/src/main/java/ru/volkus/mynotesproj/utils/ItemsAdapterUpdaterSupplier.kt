package ru.volkus.mynotesproj.utils

import java.util.function.Supplier

class ItemsAdapterUpdaterSupplier(private val type: ItemsAdapterUpdaterType): Supplier<ItemsAdapterUpdater> {
    override fun get(): ItemsAdapterUpdater {
        return when(type) {
            ItemsAdapterUpdaterType.DELETE -> NoteItemsAdapterItemDeleter()
            ItemsAdapterUpdaterType.UPDATE -> NoteItemsAdapterItemsUpdater()
            ItemsAdapterUpdaterType.ADD -> NoteItemsAdapterItemAdder()
            ItemsAdapterUpdaterType.CREATE -> NoteItemsAdapterItemsCreator()
        }
    }
}