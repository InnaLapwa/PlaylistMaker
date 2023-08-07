package com.example.playlistmaker.library.favorites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.library.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.favorites.domain.models.FavoritesState
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun getFavoritesList() {
        viewModelScope.launch {
            favoritesInteractor
                .favoritesTracks()
                .collect { list ->
                    val favoritesList = mutableListOf<Track>()
                    favoritesList.addAll(list)
                    if (favoritesList.isNotEmpty()) {
                        stateLiveData.postValue(FavoritesState.Success(favoritesList))
                    } else {
                        stateLiveData.postValue(FavoritesState.Empty)
                    }
            }
        }
    }
}