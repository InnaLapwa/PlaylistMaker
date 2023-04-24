package com.example.playlistmaker.player.domain.models

sealed interface TrackPlayerState {
    object Prepared : TrackPlayerState
    object Playing: TrackPlayerState
    object Paused: TrackPlayerState
    object Default: TrackPlayerState
}