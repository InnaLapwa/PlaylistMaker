package com.example.playlistmaker.library.playlists.domain.impl

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {
    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackInPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addTrackInPlaylist(track, playlist)
    }

    override suspend fun getPlaylistsIdsContainTrack(track: Track): List<Long> {
        return playlistsRepository.getPlaylistsIdsContainTrack(track)
    }
}