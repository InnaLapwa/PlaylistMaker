package com.example.playlistmaker.library.playlists.newPlaylist.domain.impl

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistInteractor
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistRepository

class NewPlaylistInteractorImpl(private val newPlaylistRepository: NewPlaylistRepository): NewPlaylistInteractor {
    override suspend fun savePlaylist(name: String, description: String, coverPath: String) {
        newPlaylistRepository.savePlaylist(Playlist(null, name, description, coverPath, 0))
    }
}