package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerManager
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerManager: PlayerManager): ViewModel() {

    companion object {
        private const val PLAYING_TIME_UPDATING_DELAY = 300L
    }

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        playerManager.setStateCallback { playerState ->
            renderState(playerState)
        }
    }

    fun onPlayButtonClicked() {
        when(playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerManager.isPlaying()) {
                delay(PLAYING_TIME_UPDATING_DELAY)
                if (playerState.value is PlayerState.Playing)
                    playerState.postValue(PlayerState.Playing(playerManager.getCurrentPlayerPosition()))
            }
        }
    }

    private fun startPlayer() {
        playerManager.start()
        startTimer()
    }

    fun pausePlayer() {
        playerManager.pause()
        timerJob?.cancel()
    }

    fun releasePlayer() {
        playerManager.release()
    }

    fun preparePlayer(previewUrl: String) {
        playerManager.prepare(previewUrl)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun renderState(state: PlayerState) {
        playerState.postValue(state)
    }
}