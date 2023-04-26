package com.example.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.domain.models.TrackPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentTrack = intent.getSerializableExtra("track") as Track

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]
        viewModel.preparePlayer(currentTrack.previewUrl)

        setTrackInfo(currentTrack)
        setListeners()

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeCurrentTime().observe(this) {
            updateTime(it)
        }
    }

    private fun setListeners() {
        binding.playerBack.setOnClickListener {
            finish()
        }

        binding.playerPlay.setOnClickListener {
            viewModel.switchPlayPause()
        }
    }

    private fun setTrackInfo(track: Track) {
        binding.playerTrackName.text = track.trackName
        binding.playerArtistName.text = track.artistName
        binding.playerTrackTimeData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toInt())
        binding.playerAlbumData.text = track.collectionName
        binding.playerReleaseDateData.text = track.releaseDate.substring(0,4)
        binding.playerGenreData.text = track.primaryGenreName
        binding.playerCountryData.text = track.country
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.ic_no_connection)
            .into(binding.playerCover)

        setAlbumGroupVisibility(track.collectionName != "")
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun setAlbumGroupVisibility(visible: Boolean) {
        binding.albumGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun render(state: TrackPlayerState) {
        when (state) {
            is TrackPlayerState.Prepared -> showPrepared()
            is TrackPlayerState.Playing -> showPlaying()
            is TrackPlayerState.Paused -> showPaused()
            is TrackPlayerState.Default -> showDefault()
        }
    }

    private fun updateTime(time: String) {
        binding.playerCurrentTime.text = time
    }

    private fun showDefault() {
        binding.playerPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPrepared() {
        binding.playerCurrentTime.text = "00:00"
        binding.playerPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlaying() {
        binding.playerPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPaused() {
        binding.playerPlay.setImageResource(R.drawable.ic_play)
    }
}