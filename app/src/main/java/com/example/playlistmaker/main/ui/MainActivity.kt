package com.example.playlistmaker.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker.LibraryActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.SettingsActivity
import com.example.playlistmaker.search.ui.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.button_search)
        searchButton.setOnClickListener {
            val displaySearch = Intent(this, SearchActivity::class.java)
            startActivity(displaySearch)
        }

        val libraryButton = findViewById<Button>(R.id.button_library)
        libraryButton.setOnClickListener {
            val displayLibrary = Intent(this, LibraryActivity::class.java)
            startActivity(displayLibrary)
        }

        val settingsButton = findViewById<Button>(R.id.button_settings)
        settingsButton.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}