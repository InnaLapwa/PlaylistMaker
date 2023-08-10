package com.example.playlistmaker.newPlaylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding
    private val viewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}