package com.example.playlistmaker.library.playlists.domain.db

import android.content.Intent
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackInPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistsIdsContainTrack(track: Track): List<Long>
    suspend fun getPlaylistInfo(playlistId: Long): Playlist
    suspend fun getTracksInPlaylist(playlistId: Long): List<Track>
    suspend fun deleteTrack(trackId: Long, playlistId: Long)
    suspend fun deleteTrackFromLibrary(track: Track)
    fun sharePlaylist(text: String): Intent
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String, coverPath: String)
}