package com.example.playlistmaker.library.favorites.domain.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.favorites.domain.db.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override fun favoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.favoritesTracks()
    }

    override suspend fun saveTrack(track: Track) {
        return favoritesRepository.saveTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        return favoritesRepository.deleteTrack(track)
    }
}