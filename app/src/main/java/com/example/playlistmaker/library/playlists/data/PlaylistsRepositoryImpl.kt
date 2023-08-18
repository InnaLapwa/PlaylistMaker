package com.example.playlistmaker.library.playlists.data

import com.example.playlistmaker.data.PlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
): PlaylistsRepository {
    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow{
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromTrackEntity(playlists))
    }

    override suspend fun addTrackInPlaylist(track: Track, playlist: Playlist): Boolean  {
        if (playlist.id != null) {
            appDatabase.trackLibraryDao().insertTrack(playlistDbConvertor.map(track))
            appDatabase.trackPlaylistDao().insertRelationship(playlistDbConvertor.map(track.id, playlist.id))
            appDatabase.playlistDao().updatePlaylistSize(playlist.id, playlist.size + 1)
            return true
        } else {
            return false
        }
    }

    override suspend fun getPlaylistsIdsContainTrack(track: Track): List<Long>  {
        return appDatabase.trackPlaylistDao().getPlaylistsIdsContainTrack(track.id)
    }

    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}