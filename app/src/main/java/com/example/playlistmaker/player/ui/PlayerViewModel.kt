package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.models.PlaylistsState
import com.example.playlistmaker.library.playlists.domain.models.InsertTrackInPlaylistState
import com.example.playlistmaker.player.domain.PlayerManager
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerManager: PlayerManager,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    companion object {
        private const val PLAYING_TIME_UPDATING_DELAY = 300L
    }

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    private val resultOfInsert = MutableLiveData<InsertTrackInPlaylistState>()
    fun observeInsertTrackInPlaylist(): LiveData<InsertTrackInPlaylistState> = resultOfInsert

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

    fun preparePlayer(previewUrl: String?) {
        playerManager.prepare(previewUrl)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun renderState(state: PlayerState) {
        playerState.postValue(state)
    }

    private fun renderFavoriteState(inFavorite: Boolean) {
        favoriteState.postValue(inFavorite)
    }

    fun onFavoriteClicked(track: Track) {
        if (track.isFavorite) {
            viewModelScope.launch {
                favoritesInteractor.deleteTrack(track)
                track.isFavorite = false
                renderFavoriteState(track.isFavorite)
            }
        } else {
            viewModelScope.launch {
                favoritesInteractor.saveTrack(track)
                track.isFavorite = true
                renderFavoriteState(track.isFavorite)
            }
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { list ->
                    val playlistsList = mutableListOf<Playlist>()
                    playlistsList.addAll(list)
                    if (playlistsList.isNotEmpty()) {
                        stateLiveData.postValue(PlaylistsState.Success(playlistsList))
                    } else {
                        stateLiveData.postValue(PlaylistsState.Empty)
                    }
                }
        }

    }

    fun addTrackInPlaylist(track: Track, playlist: Playlist) {
        if (playlist.id != null) {
            viewModelScope.launch {
                val playlistIds = playlistsInteractor.getPlaylistsIdsContainTrack(track)
                if (playlistIds.contains(playlist.id)) {
                    resultOfInsert.postValue(InsertTrackInPlaylistState.Error("Трек уже добавлен в плейлист " + playlist.name))

                } else {
                    playlistsInteractor.addTrackInPlaylist(track, playlist)
                    resultOfInsert.postValue(InsertTrackInPlaylistState.Success("Добавлено в плейлист " + playlist.name))
                }
            }
        }
    }

}