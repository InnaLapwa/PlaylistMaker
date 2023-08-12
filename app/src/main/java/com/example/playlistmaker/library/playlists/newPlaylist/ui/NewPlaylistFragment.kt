package com.example.playlistmaker.library.playlists.newPlaylist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment: Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding
    private val viewModel by viewModel<NewPlaylistViewModel>()

    private var coverUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { findNavController().navigateUp() }

        binding.save.setOnClickListener {
            if (binding.name.text.toString().isNotEmpty()) {
                val coverPath = saveImageToPrivateStorage(coverUri, binding.name.text.toString())
                viewModel.createPlaylist(binding.name.text.toString(), binding.description.text.toString(), coverPath)
                findNavController().navigateUp()
            }
        }

        initCoverPicker()
    }

    private fun initCoverPicker() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverUri = uri
                binding.cover.setImageURI(uri)
            } else {
                //Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.cover.setOnClickListener { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    }

    private fun saveImageToPrivateStorage(uri: Uri?, playlistName: String): String {
        if (uri == null)
            return ""

        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists_covers")
        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val file = File(filePath, playlistName + ".jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toString()
    }

}