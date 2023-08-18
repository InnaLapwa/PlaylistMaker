package com.example.playlistmaker.library.playlists.newPlaylist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val newPlaylistInteractor: NewPlaylistInteractor): ViewModel()  {
    fun createPlaylist(name: String, description: String, coverPath: String) {
        viewModelScope.launch {
            newPlaylistInteractor.savePlaylist(name, description, coverPath)
        }
    }
}