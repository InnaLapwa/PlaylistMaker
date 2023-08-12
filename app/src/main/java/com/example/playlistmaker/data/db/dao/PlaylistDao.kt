package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlist_table SET size=:size WHERE id=:playlistId")
    suspend fun updatePlaylist(playlistId: Long, size: Int)

    @Query("SELECT * FROM playlist_table ORDER BY creation_time DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>
}