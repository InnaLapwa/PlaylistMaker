package com.example.playlistmaker.search.domain.models

import com.example.playlistmaker.domain.models.Track

sealed interface TracksSearchState {
    object Loading : TracksSearchState
    object NothingFound: TracksSearchState
    object NoInternet: TracksSearchState
    object History: TracksSearchState

    data class Success(
        val tracks: List<Track>
    ) : TracksSearchState
}