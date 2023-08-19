package com.example.playlistmaker.library.playlists.newPlaylist.domain.db

interface NewPlaylistInteractor {
    suspend fun savePlaylist(name: String, description: String, coverPath: String)
}