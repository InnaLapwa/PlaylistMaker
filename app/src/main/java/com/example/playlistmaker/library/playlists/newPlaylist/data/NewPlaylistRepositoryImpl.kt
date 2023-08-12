package com.example.playlistmaker.library.playlists.newPlaylist.data

import com.example.playlistmaker.data.PlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistRepository

class NewPlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
): NewPlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

}