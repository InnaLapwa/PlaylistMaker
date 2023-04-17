package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.PlayerManager
import com.example.playlistmaker.domain.models.PlayerState

class PlayerManagerImpl: PlayerManager {
    private var mediaPlayer = MediaPlayer()
    private var state = PlayerState.DEFAULT
    private var stateCallback: ((PlayerState) -> Unit)? = null

    override fun prepare(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            updateState(PlayerState.PREPARED)
        }
    }

    override fun start() {
        mediaPlayer.start()
        updateState(PlayerState.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        updateState(PlayerState.PAUSED)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun setStateCallback(callback: (PlayerState) -> Unit) {
        stateCallback = callback
    }

    override fun getCurrentTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getState(): PlayerState {
        return state
    }

    private fun updateState(newState: PlayerState) {
        state = newState
        stateCallback?.invoke(state)
    }

}