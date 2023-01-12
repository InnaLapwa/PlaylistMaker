package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPrefs: SharedPreferences) {
    var tracksList: MutableList<Track> = get().toMutableList()

    fun get(): Array<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun add(track: Track) {
        tracksList.remove(track)
        tracksList.add(0, track)

        if (tracksList.size > HISTORY_SIZE)
            tracksList.removeAt(HISTORY_SIZE)

        val json = Gson().toJson(tracksList)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, json)
            .apply()
    }

    fun clear() {
        tracksList.clear()
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    companion object {
        const val HISTORY_SIZE = 10
    }
}