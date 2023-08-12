package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val id: Long?,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "cover_path")
    val coverPath: String,

    @ColumnInfo(name = "size")
    var size: Int,

    @ColumnInfo(name = "creation_time")
    val creationTime: Long
)
