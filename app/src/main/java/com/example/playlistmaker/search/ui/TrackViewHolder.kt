package com.example.playlistmaker.search.ui

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        Log.d("test", model.trackName)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime?.toInt() ?: 0)
        Glide.with(itemView)
             .load(model.artworkUrl100)
             .centerCrop()
             .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
             .placeholder(R.drawable.ic_no_connection)
             .into(artworkUrl100)
    }
}