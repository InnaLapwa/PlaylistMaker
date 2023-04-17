package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track

interface TrackStorage {
    fun removeTrackHistory()
    fun getTrackHistory(): Array<Track>
    fun updateTrackHistory(tracksList: MutableList<Track>)
}