package com.example.playlistmaker.di

import com.example.playlistmaker.data.PlaylistDbConvertor
import com.example.playlistmaker.data.TrackDbConvertor
import com.example.playlistmaker.library.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.library.favorites.domain.db.FavoritesRepository
import com.example.playlistmaker.library.playlists.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsRepository
import com.example.playlistmaker.library.playlists.newPlaylist.data.NewPlaylistRepositoryImpl
import com.example.playlistmaker.library.playlists.newPlaylist.domain.db.NewPlaylistRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.data.SharingRepository
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<NewPlaylistRepository> {
        NewPlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get())
    }
}