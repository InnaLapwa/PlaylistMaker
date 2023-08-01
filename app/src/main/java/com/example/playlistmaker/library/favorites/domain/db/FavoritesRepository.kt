package com.example.playlistmaker.library.favorites.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun favoritesTracks(): Flow<List<Track>>
    suspend fun saveTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun getFavoriteTracksIds(): Flow<List<Long>>
}