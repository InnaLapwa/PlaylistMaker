package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "track_playlist_table",
    primaryKeys = ["track_id", "playlist_id"],
    foreignKeys = [
        ForeignKey(
            entity = TrackLibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["track_id"]
        ),
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"]
        )
    ]
)
data class TrackPlaylistEntity(
    @ColumnInfo(name = "track_id")
    val trackId: Long,

    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,

    @ColumnInfo(name = "creation_time")
    val creationTime: Long
)
