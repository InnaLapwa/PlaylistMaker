package com.example.playlistmaker.library.playlists.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistsCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val playlistSize: TextView = itemView.findViewById(R.id.playlistSize)



    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        playlistSize.text = playlist.size.toString()

        if (playlist.coverPath.isNotEmpty()) {
            Glide.with(itemView)
                .load(playlist.coverPath)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.playlist_cover_corner_radius)))
                .placeholder(R.drawable.ic_no_connection)
                .into(cover)
        }
    }
}