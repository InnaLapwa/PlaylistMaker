package com.example.playlistmaker.creator

import com.example.playlistmaker.data.PlayerManagerImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerView

object Creator {
    fun providePresenter(view: PlayerView, currentTrack: Track): PlayerPresenter {
        return PlayerPresenter(
            view = view,
            currentTrack = currentTrack,
            playerManager = PlayerManagerImpl()
        )
    }
}