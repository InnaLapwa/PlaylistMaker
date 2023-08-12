package com.example.playlistmaker.library.playlists.domain.db

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun getPlaylists(): Flow<List<Playlist>>
}