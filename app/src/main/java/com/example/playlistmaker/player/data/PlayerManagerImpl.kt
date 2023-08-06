package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerManager
import com.example.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerManagerImpl: PlayerManager {
    private var mediaPlayer = MediaPlayer()
    private var state: PlayerState = PlayerState.Default()
    private var stateCallback: ((PlayerState) -> Unit)? = null

    override fun prepare(previewUrl: String?) {
        if (previewUrl != null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                updateState(PlayerState.Prepared())
            }
            mediaPlayer.setOnCompletionListener {
                updateState(PlayerState.Prepared())
            }
        }
    }

    override fun start() {
        if (state !is PlayerState.Default) {
            mediaPlayer.start()
            updateState(PlayerState.Playing(getCurrentPlayerPosition()))
        }
    }

    override fun pause() {
        if (state !is PlayerState.Default) {
            mediaPlayer.pause()
            updateState(PlayerState.Paused(getCurrentPlayerPosition()))
        }
    }

    override fun release() {
        mediaPlayer.release()
        updateState(PlayerState.Default())
    }

    override fun getCurrentTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying() = mediaPlayer.isPlaying

    override fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    override fun setStateCallback(callback: (PlayerState) -> Unit) {
        stateCallback = callback
    }

    private fun updateState(newState: PlayerState) {
        state = newState
        stateCallback?.invoke(state)
    }
}