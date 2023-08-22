package com.example.playlistmaker.di

import com.example.playlistmaker.data.LocalStorageImpl
import com.example.playlistmaker.domain.LocalStorage
import com.example.playlistmaker.library.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.favorites.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.playlists.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistInteractor
import com.example.playlistmaker.library.playlists.newPlaylist.domain.impl.NewPlaylistInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<LocalStorage> {
        LocalStorageImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<NewPlaylistInteractor> {
        NewPlaylistInteractorImpl(get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get(), get())
    }
}