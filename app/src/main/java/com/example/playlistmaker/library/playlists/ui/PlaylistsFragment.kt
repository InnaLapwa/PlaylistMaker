package com.example.playlistmaker.library.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()

    private val playlistsAdapter = PlaylistsCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        customizeRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        playlistsAdapter.playlists.clear()
        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun customizeRecyclerView() {
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        binding.newPlaylist.setOnClickListener { findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment) }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showNoPlaylists()
            is PlaylistsState.Success -> showSuccess(state.playlists)
        }
    }

    private fun showSuccess(playlists: List<Playlist>) {
        binding.noPlaylists.visibility = View.GONE
        binding.playlistsRecyclerView.visibility = View.VISIBLE

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun showNoPlaylists() {
        binding.noPlaylists.visibility = View.VISIBLE
        binding.playlistsRecyclerView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}