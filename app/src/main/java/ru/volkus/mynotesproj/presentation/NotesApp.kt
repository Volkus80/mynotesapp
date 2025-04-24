package ru.volkus.mynotesproj.presentation

import android.app.Application
import ru.volkus.mynotesproj.data.NotesRepo

class NotesApp: Application() {
    override fun onCreate() {
        super.onCreate()
        NotesRepo.initialize(this)
    }
}