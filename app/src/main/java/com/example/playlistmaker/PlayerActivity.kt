package com.example.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAYING_TIME_UPDATING_DELAY = 300L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

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
    lateinit var play: ImageView
    lateinit var currentTime: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val timeRunnable = Runnable { updateTime() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentTrack = intent.getSerializableExtra("track") as Track
        initializeViews()
        setListeners()

        preparePlayer()

        play.setOnClickListener {
            playbackControl()
        }
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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(timeRunnable)
            playerState = STATE_PREPARED
            currentTime.text = "00:00"
            play.setImageResource(R.drawable.ic_play)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        play.setImageResource(R.drawable.ic_pause)
        updateTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        play.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(timeRunnable)
    }

    private fun setCurrentTime() {
        currentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun updateTime() {
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerState == STATE_PLAYING) {
                        setCurrentTime()
                        handler.postDelayed(
                            this,
                            PLAYING_TIME_UPDATING_DELAY,
                        )
                    }
                }
            },
            PLAYING_TIME_UPDATING_DELAY
        )
    }
}