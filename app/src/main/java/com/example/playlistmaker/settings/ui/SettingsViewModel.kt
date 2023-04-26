package com.example.playlistmaker.settings.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Theme
import com.example.playlistmaker.domain.models.ThemeSettings

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val sharingInteractor = Creator.provideSharingInteractor(getApplication<Application>())
    private val settingsInteractor = Creator.provideSettingsInteractor(getApplication<Application>())

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

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