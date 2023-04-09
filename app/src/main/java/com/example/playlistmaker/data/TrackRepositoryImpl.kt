package com.example.playlistmaker.data

import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.TrackSearchingCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class TracksDtoResponse(val results: List<TrackDto>)

interface ITunesSearchApi {
    @GET("/search?entity=song")
    fun search(@Query("term") term: String): retrofit2.Call<TracksDtoResponse>
}

class TrackRepositoryImpl: TrackRepository {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)

    override fun getTracks(searchingText: String, callback: TrackSearchingCallback) {
        iTunesService.search(searchingText).enqueue(object :
            Callback<TracksDtoResponse> {
            override fun onResponse(
                call: Call<TracksDtoResponse>,
                response: Response<TracksDtoResponse>
            ) {
                if (response.code() == 200) {
                    var tracks = ArrayList<TrackDto>()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                    }
                    callback.onSuccess(tracks)
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<TracksDtoResponse>, t: Throwable) {
                callback.onFailure()
            }
        })
    }
}