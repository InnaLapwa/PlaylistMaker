package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val appDatabase: AppDatabase) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                emit(Resource.Success(getConvertedTrackList((response as TracksSearchResponse).results)))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    private suspend fun getConvertedTrackList(tracks: List<TrackDto>): List<Track> {
        val trackList = tracks.map { track ->
            convertTrackDto(track)
        }

        return getFavoriteMarkedTrackList(trackList).flattenToList()
    }

    override suspend fun getFavoriteMarkedTrackList(tracks: List<Track>): Flow<List<Track>> = flow{
        val favoriteTracks = appDatabase.trackDao().getTracksIds().toSet()

        val trackList = tracks.map { track ->
            track.copy(isFavorite = favoriteTracks.contains(track.id))
        }
        emit(trackList)
    }

    private suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()

    private fun convertTrackDto(trackDto: TrackDto) = Track(
        id = trackDto.id,
        trackName = trackDto.trackName,
        artistName = trackDto.artistName,
        trackTime = trackDto.trackTime,
        artworkUrl100 = trackDto.artworkUrl100,
        collectionName = trackDto.collectionName,
        releaseDate = trackDto.releaseDate,
        primaryGenreName = trackDto.primaryGenreName,
        country = trackDto.country,
        previewUrl = trackDto.previewUrl
    )
}