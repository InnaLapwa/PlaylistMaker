package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackLibraryEntity

@Dao
interface TrackLibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackLibrary: TrackLibraryEntity)

    @Delete
    suspend fun deleteTrack(trackLibrary: TrackLibraryEntity)

    @Query("SELECT * FROM track_library_table ORDER BY creation_time DESC")
    suspend fun getTracks(): List<TrackLibraryEntity>
}