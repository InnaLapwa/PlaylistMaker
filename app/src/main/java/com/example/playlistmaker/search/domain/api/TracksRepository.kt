package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}