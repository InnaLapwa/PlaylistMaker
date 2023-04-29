package com.example.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.ITunesSearchApi
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.data.PlayerManagerImpl
import com.example.playlistmaker.player.domain.PlayerManager
import com.example.playlistmaker.search.ui.SearchHistory
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
     single<PlayerManager> {
         PlayerManagerImpl()
     }

    single<ITunesSearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("PLAYLIST_MAKER_PREFERENCES", MODE_PRIVATE)
    }

    factory { Gson() }

    single<SearchHistory> {
        SearchHistory(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }


}