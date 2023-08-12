package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.TrackPlaylistEntity

@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(trackPlaylistEntity: TrackPlaylistEntity)

    @Delete
    suspend fun deleteRelationship(trackPlaylistEntity: TrackPlaylistEntity)
}