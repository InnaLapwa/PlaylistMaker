package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackLibraryEntity

@Dao
interface TrackLibraryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackLibrary: TrackLibraryEntity)

    @Delete
    suspend fun deleteTrack(trackLibrary: TrackLibraryEntity)

    @Query("DELETE FROM track_library_table WHERE id IN(SELECT id\n" +
            "FROM track_library_table tl\n" +
            "LEFT JOIN track_playlist_table tp\n" +
            "ON tl.id=tp.track_id\n" +
            "WHERE tp.playlist_id IS NULL)")
    suspend fun deleteUnusedTracks()

    @Query("SELECT * FROM track_library_table tl INNER JOIN track_playlist_table tp ON tl.id = tp.track_id WHERE tp.playlist_id=:playlistId")
    suspend fun getTracksInPlaylist(playlistId: Long): List<TrackLibraryEntity>
}