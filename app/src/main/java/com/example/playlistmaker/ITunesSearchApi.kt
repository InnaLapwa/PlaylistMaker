package com.example.playlistmaker

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {
    @GET("/search")
    fun search(@Query("term") term: String): retrofit2.Call<TracksResponse>
}