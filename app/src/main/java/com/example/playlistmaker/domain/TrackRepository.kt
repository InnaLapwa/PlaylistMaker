package com.example.playlistmaker.domain

import com.example.playlistmaker.data.TrackDto

interface TrackSearchingCallback {
    fun onSuccess(tracks: ArrayList<TrackDto>)
    fun onFailure()
}

interface TrackRepository {
    fun getTracks(searchingText: String, callback: TrackSearchingCallback)
}