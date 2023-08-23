package com.example.playlistmaker.library.playlists.data

import com.example.playlistmaker.data.PlaylistDbConvertor
import com.example.playlistmaker.data.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackLibraryEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.playlists.domain.db.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
): PlaylistsRepository {
    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow{
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addTrackInPlaylist(track: Track, playlist: Playlist): Boolean  {
        if (playlist.id != null) {
            appDatabase.trackLibraryDao().insertTrack(playlistDbConvertor.map(track))
            appDatabase.trackPlaylistDao().insertRelationship(playlistDbConvertor.map(track.id, playlist.id))
            appDatabase.playlistDao().updatePlaylistSize(playlist.id)
            return true
        } else {
            return false
        }
    }

    override suspend fun getPlaylistsIdsContainTrack(track: Track): List<Long>  {
        return appDatabase.trackPlaylistDao().getPlaylistsIdsContainTrack(track.id)
    }

    override suspend fun getPlaylistInfo(playlistId: Long): Playlist {
        return playlistDbConvertor.map(appDatabase.playlistDao().getPlaylistInfo(playlistId))
    }

    override suspend fun getTracksInPlaylist(playlistId: Long): List<Track>  {
        val trackList = appDatabase.trackLibraryDao().getTracksInPlaylist(playlistId)
        return convertFromTrackLibraryEntity(trackList)
    }

    override suspend fun deleteTrack(trackId: Long, playlistId: Long) {
        appDatabase.trackPlaylistDao().deleteRelationship(playlistDbConvertor.map(trackId, playlistId))
        appDatabase.playlistDao().updatePlaylistSize(playlistId)
    }

    override suspend fun deleteTrackFromLibrary(track: Track) {
        return appDatabase.trackLibraryDao().deleteTrack(playlistDbConvertor.map(track))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlist.id?.let { appDatabase.trackPlaylistDao().deletePlaylistsRelationships(it) }
        appDatabase.trackLibraryDao().deleteUnusedTracks()
        appDatabase.playlistDao().deletePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlistId: Long, name: String, description: String, coverPath: String) {
        appDatabase.playlistDao().updatePlaylistInfo(playlistId, name, description, coverPath)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertFromTrackLibraryEntity(trackList: List<TrackLibraryEntity>): List<Track> {
        return trackList.map { track -> trackDbConvertor.map(track) }
    }
}