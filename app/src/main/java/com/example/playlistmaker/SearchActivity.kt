package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private var inputText = ""
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private lateinit var tracksList: RecyclerView
    private lateinit var queryInput: EditText
    private lateinit var noInternetLayout: LinearLayout
    private lateinit var nothingFoundLayout: LinearLayout

    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.et_search)
        tracksList = findViewById(R.id.search_recyclerView)
        noInternetLayout = findViewById(R.id.search_no_internet_layout)
        nothingFoundLayout = findViewById(R.id.search_nothing_found_layout)

        val backLayout = findViewById<FrameLayout>(R.id.fl_search_back)
        backLayout.setOnClickListener {
            finish()
        }

        val inputSearch = findViewById<EditText>(R.id.et_search)
        val clearImage = findViewById<ImageView>(R.id.iv_search_clear)
        clearImage.setOnClickListener {
            inputSearch.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            tracks.clear()
            onSearchingResult()
        }

        val updateButton = findViewById<Button>(R.id.search_update_button)
        updateButton.setOnClickListener {
            findTracks()
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                clearImage.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputSearch.setText(inputText)
        inputSearch.addTextChangedListener(inputTextWatcher)

        val searchRecyclerView = findViewById<RecyclerView>(R.id.search_recyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)

        val trackAdapter = TrackAdapter()
        searchRecyclerView.adapter = trackAdapter

        adapter.tracks = tracks
        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks()
                true
            }
            false
        }
    }

    private fun findTracks() {
        iTunesService.search(queryInput.text.toString()).enqueue(object :
            Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        onSearchingResult()
                    }
                    if (tracks.isEmpty()) {
                        onNothingFound()
                    }
                } else {
                    onNoInternet()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                onNoInternet()
            }
        })
    }

    private fun onSearchingResult() {
        adapter.notifyDataSetChanged()
        noInternetLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
    }

    private fun onNothingFound() {
        adapter.notifyDataSetChanged()
        noInternetLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.VISIBLE
    }

    private fun onNoInternet() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        noInternetLayout.visibility = View.VISIBLE
        nothingFoundLayout.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SEARCH_TEXT, "")
    }

    private fun clearButtonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
}