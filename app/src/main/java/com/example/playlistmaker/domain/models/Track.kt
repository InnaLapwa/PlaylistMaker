package com.example.playlistmaker.domain.models

import java.io.Serializable


data class Track(
    val id: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String?, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String?, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String?, // Ссылка на отрывок
    var isFavorite: Boolean = false
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}