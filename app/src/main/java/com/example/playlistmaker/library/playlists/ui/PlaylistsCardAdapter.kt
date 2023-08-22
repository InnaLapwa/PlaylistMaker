package com.example.playlistmaker.library.playlists.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistsCardAdapter: RecyclerView.Adapter<PlaylistsCardViewHolder>() {
    var playlists = mutableListOf<Playlist>()
    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist_card, parent, false)
        return PlaylistsCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsCardViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
        }
    }
}