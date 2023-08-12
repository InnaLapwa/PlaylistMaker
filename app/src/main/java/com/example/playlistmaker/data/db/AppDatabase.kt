package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TrackLibraryDao
import com.example.playlistmaker.data.db.dao.TrackPlaylistDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TrackLibraryEntity
import com.example.playlistmaker.data.db.entity.TrackPlaylistEntity

@Database(version = 1, entities = [
    TrackEntity::class,
    PlaylistEntity::class,
    TrackLibraryEntity::class,
    TrackPlaylistEntity::class
])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackLibraryDao(): TrackLibraryDao
    abstract fun trackPlaylistDao(): TrackPlaylistDao
}