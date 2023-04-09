package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.SEARCH_HISTORY
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.TrackStorage
import com.google.gson.Gson

class TrackStorageImpl(val sharedPrefs: SharedPreferences, val gson: Gson): TrackStorage {

    override fun removeTrackHistory() {
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    override fun getTrackHistory(): Array<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return emptyArray()
        return gson.fromJson(json, Array<Track>::class.java)
    }

    override fun updateTrackHistory(tracksList: MutableList<Track>) {
        if (tracksList.size > SearchHistory.HISTORY_SIZE)
            tracksList.removeAt(SearchHistory.HISTORY_SIZE)

        val json = gson.toJson(tracksList)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, json)
            .apply()
    }

}