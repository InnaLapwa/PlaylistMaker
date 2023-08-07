package com.example.playlistmaker.library.favorites.domain.models

import com.example.playlistmaker.domain.models.Track

sealed interface FavoritesState {
    object Empty: FavoritesState

    data class Success(
        val tracks: List<Track>
    ) : FavoritesState
}