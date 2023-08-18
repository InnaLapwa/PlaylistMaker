package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.TrackLibraryEntity

@Dao
interface TrackLibraryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackLibrary: TrackLibraryEntity)

    @Delete
    suspend fun deleteTrack(trackLibrary: TrackLibraryEntity)
}