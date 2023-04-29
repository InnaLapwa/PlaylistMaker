package com.example.playlistmaker.settings.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Theme
import com.example.playlistmaker.domain.models.ThemeSettings
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor,
                        private val settingsInteractor: SettingsInteractor): ViewModel() {

    fun shareApp(): Intent {
        return sharingInteractor.shareApp()
    }

    fun openTerms(): Intent {
        return sharingInteractor.openTerms()
    }

    fun openSupport(): Intent {
        return sharingInteractor.openSupport()
    }

    private fun getThemeSettings(): ThemeSettings {
        return settingsInteractor.getThemeSettings()
    }

    fun updateThemeSetting(settings: ThemeSettings) {
        settingsInteractor.updateThemeSetting(settings)
    }

    fun isNightModeChecked(): Boolean {
        return getThemeSettings().theme == Theme.DARK
    }
}