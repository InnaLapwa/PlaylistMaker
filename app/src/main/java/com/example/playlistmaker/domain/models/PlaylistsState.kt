package com.example.playlistmaker.domain.models

sealed interface PlaylistsState {
    object Empty: PlaylistsState

    data class Success(
        val playlists: List<Playlist>
    ) : PlaylistsState
}