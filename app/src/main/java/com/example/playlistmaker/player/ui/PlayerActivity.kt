package com.example.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
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
import com.example.playlistmaker.util.debounceActionDelay
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var currentTrack: Track
    private val playlistsAdapter = PlaylistsListAdapter()

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = intent.getSerializableExtra("track") as Track

        viewModel.preparePlayer(currentTrack.previewUrl)

        viewModel.observeFavoriteState().observe(this) {
            binding.addToFavorite.setImageResource(if (it) R.drawable.ic_in_favorite else R.drawable.ic_add_to_favorite)
        }

        viewModel.observeInsertTrackInPlaylist().observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            if (it.hideBottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.observePlayerState().observe(this) {
            binding.playerPlay.isEnabled = it.isPlayButtonEnabled
            binding.playerCurrentTime.text = it.progress
            when (it) {
                is PlayerState.Playing -> binding.playerPlay.setImageResource(R.drawable.ic_pause)
                else -> binding.playerPlay.setImageResource(R.drawable.ic_play)
            }
        }

        viewModel.observeState().observe(this) {
            render(it)
        }

        setTrackInfo(currentTrack)
        setListeners()
        initBottomSheet()
    }

    fun hideBottomSheet(fragment: NewPlaylistFragment) {
        binding.scrollView.isVisible = true
        binding.playlistsBottomSheet.isVisible = true
        binding.overlay.isVisible = true

        binding.fragmentContainer.isVisible = false
        playlistsAdapter.playlists.clear()
        viewModel.getPlaylists()
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

        binding.newPlaylist.setOnClickListener {
            val newPlaylistFragment = NewPlaylistFragment()
            val argTrackId = Bundle()
            argTrackId.putString(CURRENT_TRACK_ID, currentTrack.id.toString())
            newPlaylistFragment.arguments = argTrackId

            binding.scrollView.isVisible = false
            binding.playlistsBottomSheet.isVisible = false
            binding.overlay.isVisible = false
            binding.fragmentContainer.isVisible = true

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, newPlaylistFragment)
                .commit()
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
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
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

        playlistsAdapter.onItemClick = { playlist ->
            onPlaylistClickDebounce(playlist)
        }

        onPlaylistClickDebounce = debounceActionDelay<Playlist>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { playlist ->
            viewModel.addTrackInPlaylist(currentTrack, playlist)
        }
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

    companion object {
        const val CURRENT_TRACK_ID = "CURRENT_TRACK_ID"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}