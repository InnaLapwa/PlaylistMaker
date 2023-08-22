package com.example.playlistmaker.library.playlists.currentPlaylist.domain.models

import android.content.Intent
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

sealed interface CurrentPlaylistState {
    object Empty: CurrentPlaylistState

    object Deleted: CurrentPlaylistState

    data class Success(
        val playlist: Playlist,
        val tracks: List<Track>
    ) : CurrentPlaylistState

    data class Share(
        val intent: Intent
    ) : CurrentPlaylistState
}