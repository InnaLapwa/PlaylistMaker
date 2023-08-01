package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.favorites.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.TracksSearchState
import com.example.playlistmaker.util.debounceDelayAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor,
                            private val history: SearchHistory,
                            private val favoritesRepository: FavoritesRepository): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var lastSearchText: String? = null
    private var searchJob: Job? = null

    private val tracksSearchDebounce = debounceDelayAction<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        findTracks(changedText)
    }

    private val stateLiveData = MutableLiveData<TracksSearchState>()
    fun observeState(): LiveData<TracksSearchState> = stateLiveData

    private val historyState = MutableLiveData<List<Track>>()
    fun observeHistoryState(): LiveData<List<Track>> = historyState

    fun addTrackToHistory(track: Track) {
        history.add(track)
    }

    private fun findTracks(newSearchText: String) {
        setLastSearchText(newSearchText)
        if (newSearchText.isEmpty()) return

        renderState(TracksSearchState.Loading)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            tracksInteractor
                .searchTracks(newSearchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
            searchJob = null
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
            renderState(TracksSearchState.Success(foundTracks))
        }
        if (errorMessage != null) {
            renderState(TracksSearchState.NoInternet)
        } else if (tracks.isEmpty()) {
            renderState(TracksSearchState.NothingFound)
        } else {
            renderState(TracksSearchState.Success(tracks))
        }
    }

    fun isHistoryEmpty() = history.tracksList.isEmpty()

    fun clearHistory() {
        history.clear()
        renderState(TracksSearchState.History)
    }

    fun clearSearchingText() {
        getHistoryList()
        renderState(TracksSearchState.History)
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText != changedText) {
            lastSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    fun startSearch() {
        val newSearchText = lastSearchText
        findTracks(newSearchText ?: "")
    }

    private fun setLastSearchText(changedText: String) {
        this.lastSearchText = changedText
    }

    fun getLastSearchText(): String {
        return lastSearchText ?: ""
    }

    fun getHistoryList() {
        getFavoritesIdsList(history.tracksList)
    }

    private fun renderState(state: TracksSearchState) {
        stateLiveData.postValue(state)
    }

    private fun getFavoritesIdsList(tracks: List<Track>) {
        viewModelScope.launch {
            favoritesRepository
                .getFavoriteTracksIds()
                .collect { list ->
                    updateFavoriteMark(tracks, list)
                }
        }
    }

    private fun updateFavoriteMark(tracks: List<Track>, idsList: List<Long>) {
        var trackList = mutableListOf<Track>()
        for (track in tracks) {
            trackList.add(track.copy(isFavorite = idsList.contains(track.id)))
        }
        historyState.postValue(trackList)
    }
}