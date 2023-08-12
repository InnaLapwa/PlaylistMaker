package com.example.playlistmaker.player.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistsState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.playlists.newPlaylist.ui.NewPlaylistFragment
import com.example.playlistmaker.player.domain.models.PlayerState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var currentTrack: Track
    private val playlistsAdapter = PlaylistsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = intent.getSerializableExtra("track") as Track

        viewModel.preparePlayer(currentTrack.previewUrl)

        viewModel.observeFavoriteState().observe(this) {
            binding.addToFavorite.setImageResource(if (it) R.drawable.ic_in_favorite else R.drawable.ic_add_to_favorite)
        }

        setTrackInfo(currentTrack)
        setListeners()
        initBottomSheet()

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        viewModel.observePlayerState().observe(this) {
            binding.playerPlay.isEnabled = it.isPlayButtonEnabled
            binding.playerCurrentTime.text = it.progress
            when (it) {
                is PlayerState.Playing -> binding.playerPlay.setImageResource(R.drawable.ic_pause)
                else -> binding.playerPlay.setImageResource(R.drawable.ic_play)
            }
        }

        binding.newPlaylist.setOnClickListener {

        }


    }

    private fun setListeners() {
        binding.playerBack.setOnClickListener {
            finish()
        }

        binding.playerPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.addToFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(currentTrack)
       }
    }

    private fun setTrackInfo(track: Track) {
        binding.playerTrackName.text = track.trackName
        binding.playerArtistName.text = track.artistName
        binding.playerTrackTimeData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime?.toInt() ?: 0)
        binding.playerAlbumData.text = track.collectionName
        binding.playerReleaseDateData.text = track.releaseDate?.substring(0,4)
        binding.playerGenreData.text = track.primaryGenreName
        binding.playerCountryData.text = track.country
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.ic_no_connection)
            .into(binding.playerCover)

        setAlbumGroupVisibility(track.collectionName != "")

        binding.addToFavorite.setImageResource(if (currentTrack.isFavorite) R.drawable.ic_in_favorite else R.drawable.ic_add_to_favorite)
    }

    private fun initBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.addToPlaylist.setOnClickListener {
            playlistsAdapter.playlists.clear()
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })
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

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showNoPlaylists()
            is PlaylistsState.Success -> showSuccess(state.playlists)
        }
    }

    private fun showSuccess(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun showNoPlaylists() {
        binding.playlistsRecyclerView.visibility = View.GONE
    }
}