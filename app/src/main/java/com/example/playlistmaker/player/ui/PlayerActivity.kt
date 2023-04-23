package com.example.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerView

class PlayerActivity : AppCompatActivity(), PlayerView {
    private lateinit var presenter: PlayerPresenter

    private lateinit var back: FrameLayout
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var cover: ImageView
    private lateinit var album: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var albumGroup: Group
    private lateinit var play: ImageView
    private lateinit var currentTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initializePresenter()
        initializeViews()
        setListeners()
    }

    private fun initializePresenter() {
        presenter = Creator.providePresenter(
            view = this,
            currentTrack = intent.getSerializableExtra("track") as Track
        )
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
        play = findViewById(R.id.player_play)
        currentTime = findViewById(R.id.player_current_time)

        presenter.setTrackInfo()
    }

    private fun setListeners() {
        back.setOnClickListener {
            finish()
        }

        play.setOnClickListener {
            presenter.switchPlayPause()
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.releasePlayer()
        presenter.onViewDestroyed()
    }

    override fun setTrackName(name: String) {
        trackName.text = name
    }

    override fun setArtistName(name: String) {
        artistName.text = name
    }

    override fun setTrackTime(time: String) {
        trackTime.text = time
    }

    override fun setAlbumName(name: String) {
        album.text = name
    }

    override fun setReleaseDate(date: String) {
        releaseDate.text = date
    }

    override fun setGenre(name: String) {
        genre.text = name
    }

    override fun setCountry(name: String) {
        country.text = name
    }

    override fun setCover(coverPath: String) {
        Glide.with(applicationContext)
            .load(coverPath)
            .centerCrop()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.ic_no_connection)
            .into(cover)
    }

    override fun setAlbumGroupVisibility(visible: Boolean) {
        albumGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setPlayerStateStart() {
        play.setImageResource(R.drawable.ic_pause)
    }

    override fun setPlayerStatePause() {
        play.setImageResource(R.drawable.ic_play)
    }

    override fun setPlayerStatePrepared() {
        currentTime.text = "00:00"
        play.setImageResource(R.drawable.ic_play)
    }

    override fun setCurrentTime(time: String) {
        currentTime.text = time
    }

}