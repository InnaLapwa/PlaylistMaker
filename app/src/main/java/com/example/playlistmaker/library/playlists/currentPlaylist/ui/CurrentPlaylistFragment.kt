package com.example.playlistmaker.library.playlists.currentPlaylist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCurrentPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.playlists.currentPlaylist.domain.models.CurrentPlaylistState
import com.example.playlistmaker.library.playlists.newPlaylist.ui.NewPlaylistFragment
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounceActionDelay
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrentPlaylistFragment : Fragment() {
    private var _binding: FragmentCurrentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CurrentPlaylistViewModel>()

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val tracksAdapter = TrackAdapter()
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var currentPlaylist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylistInfo(requireArguments().getLong(PLAYLIST_ID))

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        initBottomSheet()

        binding.playlistBack.setOnClickListener { goBack() }
        binding.playlistShare.setOnClickListener { viewModel.sharePlaylist(currentPlaylist, tracksAdapter.tracks) }
        binding.playlistMenu.setOnClickListener { menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
        binding.playlistMenuShare.setOnClickListener { viewModel.sharePlaylist(currentPlaylist, tracksAdapter.tracks) }
        binding.playlistMenuEdit.setOnClickListener { editPlaylist() }
        binding.playlistMenuDelete.setOnClickListener {
            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить плейлист")
                .setMessage("Хотите удалить плейлист \"${currentPlaylist.name}\"?")
                .setNeutralButton("Нет") { dialog, which ->
                }.setPositiveButton("Да") { dialog, which ->
                    viewModel.deletePlaylist(currentPlaylist)
                }
            confirmDialog.show()
        }
    }

    private fun editPlaylist() {
        currentPlaylist.id?.let {
            findNavController()
                .navigate(
                    R.id.action_currentPlaylistFragment_to_newPlaylistFragment,
                    NewPlaylistFragment.createArgs(it)
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBottomSheet() {
        binding.playlistTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistTracksRecyclerView.adapter = tracksAdapter

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet)
        tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        tracksBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) { }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })

        tracksAdapter.onItemClick = { track ->
            onTrackClickDebounce(track)
        }

        tracksAdapter.onLongItemClick = { track ->
            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.current_playlist_question_title))
                .setMessage(getString(R.string.current_playlist_question_message))
                .setNeutralButton(getString(R.string.current_playlist_question_cancel)) { dialog, which ->
                }.setPositiveButton(getString(R.string.current_playlist_question_delete)) { dialog, which ->
                    viewModel.deleteTrack(track, requireArguments().getLong(PLAYLIST_ID))
                }
                confirmDialog.show()
            true
        }

        onTrackClickDebounce = debounceActionDelay<Track>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { track ->
            val player = Intent(requireContext(), PlayerActivity::class.java)
            player.putExtra("track", track)
            startActivity(player)
        }

        binding.playlistTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistTracksRecyclerView.adapter = tracksAdapter

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        fillPlaylistMenu()
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })
    }

    private fun render(state: CurrentPlaylistState) {
        when (state) {
            is CurrentPlaylistState.Success -> showSuccess(state.playlist, state.tracks)
            is CurrentPlaylistState.Empty -> showEmpty()
            is CurrentPlaylistState.Share -> showShareMenu(state.intent)
            is CurrentPlaylistState.Deleted -> goBack()
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun showShareMenu(intent: Intent) {
        startActivity(intent)
    }

    private fun showEmpty() {
        Toast.makeText(requireContext(), getString(R.string.current_playlist_share_empty), Toast.LENGTH_LONG).show()
    }


    private fun showSuccess(playlist: Playlist, tracks: List<Track>) {
        currentPlaylist = playlist
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDuration.text = viewModel.countPlaylistDuration(tracks) + " " + getString(R.string.current_playlist_duration_text)
        binding.playlistSize.text = playlist.size.toString() + " " + getString(R.string.current_playlist_size_text)
        Glide.with(requireContext())
            .load(playlist.coverPath)
            .centerCrop()
            .placeholder(R.drawable.ic_no_connection)
            .into(binding.playlistCover)

        if (playlist.description.isEmpty())
            binding.playlistDescription.visibility = View.GONE

        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun fillPlaylistMenu() {
        binding.menuPlaylistName.text = currentPlaylist.name
        binding.menuPlaylistSize.text = currentPlaylist.size.toString() + " " + getString(R.string.current_playlist_size_text)
        Glide.with(requireContext())
            .load(currentPlaylist.coverPath)
            .transform(RoundedCorners(8))
            .centerCrop()
            .placeholder(R.drawable.ic_no_connection)
            .into(binding.menuPlaylistCover)
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_ID to playlistId)
    }
}