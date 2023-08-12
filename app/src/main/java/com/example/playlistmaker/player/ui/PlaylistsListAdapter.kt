package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistsListAdapter: RecyclerView.Adapter<PlaylistsListViewHolder>() {
    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist_list, parent, false)
        return PlaylistsListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsListViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}