package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.TracksSearchState

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor,
                            private val history: SearchHistory): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }


    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val stateLiveData = MutableLiveData<TracksSearchState>()
    fun observeState(): LiveData<TracksSearchState> = stateLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun addTrackToHistory(track: Track) {
        history.add(track)
    }

    private fun findTracks(newSearchText: String) {
        setLastSearchText(newSearchText)
        if (newSearchText.isEmpty()) return

        renderState(TracksSearchState.Loading)
        tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
        })
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
        if (lastSearchText == changedText) {
            return
        }

        this.lastSearchText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { findTracks(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
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