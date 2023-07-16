package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.TracksSearchState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor,
                            private val history: SearchHistory): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var lastSearchText: String? = null

    private val tracksSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        findTracks(changedText)
    }

    private val stateLiveData = MutableLiveData<TracksSearchState>()
    fun observeState(): LiveData<TracksSearchState> = stateLiveData

    fun addTrackToHistory(track: Track) {
        history.add(track)
    }

    private fun findTracks(newSearchText: String) {
        setLastSearchText(newSearchText)
        if (newSearchText.isEmpty()) return

        renderState(TracksSearchState.Loading)

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(newSearchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
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

    fun setLastSearchText(changedText: String) {
        this.lastSearchText = changedText
    }

    fun getLastSearchText(): String {
        return lastSearchText ?: ""
    }

    fun getHistoryList(): MutableList<Track> {
        return history.tracksList
    }

    private fun renderState(state: TracksSearchState) {
        stateLiveData.postValue(state)
    }
}