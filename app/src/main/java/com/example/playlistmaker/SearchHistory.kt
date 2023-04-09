package com.example.playlistmaker

import com.example.playlistmaker.domain.TrackStorage
import com.example.playlistmaker.domain.models.Track

class SearchHistory(val trackStorage: TrackStorage) {
    var tracksList: MutableList<Track> = get().toMutableList()

    fun get(): Array<Track> {
        return trackStorage.getTrackHistory()
    }

    fun add(track: Track) {
        tracksList.remove(track)
        tracksList.add(0, track)

        if (tracksList.size > HISTORY_SIZE)
            tracksList.removeAt(HISTORY_SIZE)

        trackStorage.updateTrackHistory(tracksList)
    }

    fun clear() {
        tracksList.clear()
        trackStorage.removeTrackHistory()
    }

    companion object {
        const val HISTORY_SIZE = 10
    }
}