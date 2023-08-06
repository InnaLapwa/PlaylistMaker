package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "track_name")
    val trackName: String, // Название композиции

    @ColumnInfo(name = "artist_name")
    val artistName: String, // Имя исполнителя

    @ColumnInfo(name = "track_time")
    val trackTime: String?, // Продолжительность трека

    @ColumnInfo(name = "artwork_url100")
    val artworkUrl100: String, // Ссылка на изображение обложки

    @ColumnInfo(name = "collection_name")
    val collectionName: String, // Название альбома

    @ColumnInfo(name = "release_date")
    val releaseDate: String?, // Год релиза трека

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String, // Жанр трека

    @ColumnInfo(name = "country")
    val country: String, // Страна исполнителя

    @ColumnInfo(name = "preview_url")
    val previewUrl: String?, // Ссылка на отрывок

    @ColumnInfo(name = "creation_time")
    val creationTime: Long
)
