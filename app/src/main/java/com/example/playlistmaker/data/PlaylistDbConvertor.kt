package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

class PlaylistDbConvertor() {
    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.size
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.size,
            System.currentTimeMillis()
        )
    }

}