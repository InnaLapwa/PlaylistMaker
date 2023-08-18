package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackPlaylistEntity

@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelationship(trackPlaylistEntity: TrackPlaylistEntity)

    @Delete
    suspend fun deleteRelationship(trackPlaylistEntity: TrackPlaylistEntity)

    @Query("SELECT playlist_id FROM track_playlist_table WHERE track_id=:trackId")
    suspend fun getPlaylistsIdsContainTrack(trackId: Long): List<Long>
}