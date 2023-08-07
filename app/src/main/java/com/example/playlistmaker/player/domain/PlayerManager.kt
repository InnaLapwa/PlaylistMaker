package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.models.PlayerState

interface PlayerManager {
    fun prepare(previewUrl: String?)
    fun start()
    fun pause()
    fun release()
    fun getCurrentTime(): Int
    fun isPlaying(): Boolean
    fun getCurrentPlayerPosition(): String
    fun setStateCallback(callback: (PlayerState) -> Unit)
}