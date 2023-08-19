package com.example.playlistmaker.library.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.favorites.domain.models.FavoritesState
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounceActionDelay
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoritesViewModel>()

    private val favoritesAdapter = TrackAdapter()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter.tracks.clear()
        viewModel.getFavoritesList()

        onTrackClickDebounce = debounceActionDelay<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val player = Intent(requireContext(), PlayerActivity::class.java)
            player.putExtra("track", track.copy(isFavorite = true))
            startActivity(player)
        }

        favoritesAdapter.onItemClick = { track ->
            onTrackClickDebounce(track)
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.adapter = favoritesAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getFavoritesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showNothingFound()
            is FavoritesState.Success -> showSuccess(state.tracks)
        }
    }

    private fun showSuccess(tracks: List<Track>) {
        binding.emptyFavorites.visibility = View.GONE
        binding.favoritesRecyclerView.visibility = View.VISIBLE

        favoritesAdapter.tracks.clear()
        favoritesAdapter.tracks.addAll(tracks)
        favoritesAdapter.notifyDataSetChanged()
    }

    private fun showNothingFound() {
        binding.emptyFavorites.visibility = View.VISIBLE
        binding.favoritesRecyclerView.visibility = View.GONE
    }



    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}