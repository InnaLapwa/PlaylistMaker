package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.ui.TracksSearchViewModel
import com.example.playlistmaker.player.data.PlayerManagerImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerView
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl

object Creator {
    private fun getMoviesRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getMoviesRepository(context))
    }

    fun providePresenter(view: PlayerView, currentTrack: Track): PlayerPresenter {
        return PlayerPresenter(
            view = view,
            currentTrack = currentTrack,
            playerManager = PlayerManagerImpl()
        )
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
}