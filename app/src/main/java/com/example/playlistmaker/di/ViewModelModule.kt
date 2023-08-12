package com.example.playlistmaker.di

import com.example.playlistmaker.library.favorites.ui.FavoritesViewModel
import com.example.playlistmaker.library.playlists.newPlaylist.ui.NewPlaylistViewModel
import com.example.playlistmaker.library.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.TracksSearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        TracksSearchViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}