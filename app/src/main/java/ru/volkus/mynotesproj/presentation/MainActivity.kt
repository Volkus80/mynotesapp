package ru.volkus.mynotesproj.presentation

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.volkus.mynotesproj.R

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "MainActivity started")
        setContentView(R.layout.activity_main)
    }
}