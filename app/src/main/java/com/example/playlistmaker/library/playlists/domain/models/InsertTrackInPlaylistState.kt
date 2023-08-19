package com.example.playlistmaker.library.playlists.domain.models

sealed class InsertTrackInPlaylistState(val hideBottomSheet: Boolean, val message: String) {

    class Success(message: String): InsertTrackInPlaylistState(true, message)

    class Error(message: String): InsertTrackInPlaylistState(false, message)
}
