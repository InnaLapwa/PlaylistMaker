package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.PlayerManager
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerPresenter(
    private var view: PlayerView?,
    private val currentTrack: Track,
    private val playerManager: PlayerManager
    ) {

    companion object {
        private const val PLAYING_TIME_UPDATING_DELAY = 300L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timeRunnable = Runnable { updateTime() }

    init {
        playerManager.prepare(currentTrack.previewUrl)
        playerManager.setStateCallback { playerState ->
            when (playerState) {
                PlayerState.PREPARED -> {
                    handler.removeCallbacks(timeRunnable)
                    view?.setPlayerStatePrepared()
                }
                PlayerState.PLAYING -> {
                    view?.setPlayerStateStart()
                    updateTime()
                }
                PlayerState.PAUSED -> {
                    view?.setPlayerStatePause()
                }
                else -> {}
            }
        }
    }

    fun setTrackInfo() {
        view?.setTrackName(currentTrack.trackName)
        view?.setArtistName(currentTrack.artistName)
        view?.setTrackTime(SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime.toInt()))
        view?.setAlbumName(currentTrack.collectionName)
        view?.setReleaseDate(currentTrack.releaseDate.substring(0,4))
        view?.setGenre(currentTrack.primaryGenreName)
        view?.setCountry(currentTrack.country)
        view?.setCover(currentTrack.getCoverArtwork())
        view?.setAlbumGroupVisibility(currentTrack.collectionName != "")
    }

    fun setCurrentTime() {
        val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerManager.getCurrentTime())
        view?.setCurrentTime(currentTime)
    }

    private fun updateTime() {
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerManager.getState() == PlayerState.PLAYING) {
                        setCurrentTime()
                        handler.postDelayed(
                            this,
                            PLAYING_TIME_UPDATING_DELAY
                        )
                    }
                }
            },
            PLAYING_TIME_UPDATING_DELAY
        )
    }

    fun switchPlayPause() {
        when (playerManager.getState()) {
            PlayerState.PLAYING -> {
                playerManager.pause()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                playerManager.start()
            }
            else -> {}
        }
    }

    fun pausePlayer() {
        playerManager.pause()
        handler.removeCallbacks(timeRunnable)
    }

    fun releasePlayer() {
        playerManager.release()
        handler.removeCallbacks(timeRunnable)
    }

    fun onViewDestroyed() {
        view = null
    }
}