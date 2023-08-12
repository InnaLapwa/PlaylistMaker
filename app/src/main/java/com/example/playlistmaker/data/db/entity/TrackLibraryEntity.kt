package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_library_table")
data class TrackLibraryEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTime: String?,

    @ColumnInfo(name = "artwork_url100")
    val artworkUrl100: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "preview_url")
    val previewUrl: String?,

    @ColumnInfo(name = "creation_time")
    val creationTime: Long
)