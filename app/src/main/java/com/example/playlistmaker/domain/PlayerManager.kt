package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerManager {
    fun prepare(previewUrl: String)
    fun start()
    fun pause()
    fun release()
    fun setStateCallback(callback: (PlayerState) -> Unit)
    fun switchPlayPause()
    fun getCurrentTime(): Int
    fun getState(): PlayerState
}