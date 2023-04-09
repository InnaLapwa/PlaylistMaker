package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorageImpl
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.TrackStorage
import com.google.gson.Gson

class App : Application() {
    private var darkTheme = false

    private lateinit var trackRepository: TrackRepositoryImpl
    private lateinit var trackStorage: TrackStorageImpl
    private var gson = Gson()

    override fun onCreate() {
        super.onCreate()
        instance = this

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_SWITCHER, false)
        switchTheme(darkTheme)

        trackStorage = TrackStorageImpl(sharedPrefs, gson)
        trackRepository = TrackRepositoryImpl()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getTrackStorage(): TrackStorage {
        return trackStorage
    }

    fun getTrackRepository(): TrackRepository {
        return trackRepository
    }

    companion object {
        lateinit var instance: App
            private set
    }
}