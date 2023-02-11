package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    lateinit var back: ImageView
    lateinit var currentTrack: Track
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackTime: TextView
    lateinit var cover: ImageView
    lateinit var album: TextView
    lateinit var releaseDate: TextView
    lateinit var genre: TextView
    lateinit var country: TextView
    lateinit var albumGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentTrack = intent.getSerializableExtra("track") as Track
        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        back = findViewById(R.id.player_back)
        trackName = findViewById(R.id.player_track_name)
        artistName = findViewById(R.id.player_artist_name)
        trackTime = findViewById(R.id.player_track_time_data)
        cover = findViewById(R.id.player_cover)
        album = findViewById(R.id.player_album_data)
        releaseDate = findViewById(R.id.player_release_date_data)
        genre = findViewById(R.id.player_genre_data)
        country = findViewById(R.id.player_country_data)
        albumGroup = findViewById(R.id.album_group)

        if (currentTrack != null) {
            Glide.with(applicationContext)
                .load(currentTrack.getCoverArtwork())
                .centerCrop()
                .transform(RoundedCorners(8))
                .placeholder(R.drawable.ic_no_connection)
                .into(cover)

            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime.toInt())
            album.text = currentTrack.collectionName
            releaseDate.text = currentTrack.releaseDate.substring(0,4)
            genre.text = currentTrack.primaryGenreName
            country.text = currentTrack.country

            albumGroup.visibility = if (currentTrack.collectionName == "") View.GONE else View.VISIBLE
        }
    }

    private fun setListeners() {
        back.setOnClickListener {
            finish()
        }
    }
}