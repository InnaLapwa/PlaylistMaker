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

    enum class SearchingResultStatus {
        SUCCESS,
        NOTHING_FOUND,
        NO_INTERNET
    }

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var inputSearch: EditText
    private lateinit var somethingWentWrongLayout: LinearLayout
    private lateinit var backLayout: FrameLayout
    private lateinit var clearImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyClearButton: Button
    private lateinit var history: SearchHistory
    private lateinit var historyLayout: LinearLayout

    private var inputText = ""
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)
    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        history = SearchHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))

        initializeViews()
        setListeners()
        customizeRecyclerView()
        setHistoryVisibility()
    }

    private fun initializeViews() {
        inputSearch = findViewById(R.id.et_search)
        searchRecyclerView = findViewById(R.id.search_recyclerView)
        somethingWentWrongLayout = findViewById(R.id.search_something_went_wrong)
        backLayout = findViewById(R.id.fl_search_back)
        clearImage = findViewById(R.id.iv_search_clear)
        updateButton = findViewById(R.id.search_update_button)
        errorImage = findViewById(R.id.search_error_image)
        errorText = findViewById(R.id.search_error_text)
        historyRecyclerView = findViewById(R.id.search_history_recyclerView)
        historyClearButton = findViewById(R.id.search_history_clear)
        historyLayout = findViewById(R.id.search_history_layout)
    }

    private fun setListeners() {
        backLayout.setOnClickListener {
            finish()
        }

        clearImage.setOnClickListener {
            inputSearch.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            tracks.clear()
            onSearchingResult(SearchingResultStatus.SUCCESS)
            setHistoryVisibility()
        }

        updateButton.setOnClickListener {
            findTracks()
        }

        historyClearButton.setOnClickListener {
            history.clear()
            historyAdapter.notifyDataSetChanged()
            setHistoryVisibility()
        }

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)

                findTracks()
                true
            }
            else false
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                clearImage.visibility = clearButtonVisibility(s)
                historyLayout.visibility = if (inputSearch.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputSearch.setText(inputText)
        inputSearch.addTextChangedListener(inputTextWatcher)
    }

    private fun customizeRecyclerView() {
        trackAdapter.onItemClick = { track ->
            history.add(track)
            historyAdapter.notifyDataSetChanged()
        }

        trackAdapter.tracks = tracks
        searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchRecyclerView.adapter = trackAdapter

        historyAdapter.tracks = history.tracksList
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun findTracks() {
        iTunesService.search(inputSearch.text.toString()).enqueue(object :
            Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        onSearchingResult(SearchingResultStatus.SUCCESS)
                    }
                    if (tracks.isEmpty()) {
                        onSearchingResult(SearchingResultStatus.NOTHING_FOUND)
                    }
                } else {
                    onSearchingResult(SearchingResultStatus.NO_INTERNET)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                onSearchingResult(SearchingResultStatus.NO_INTERNET)
            }
        })
    }

    private fun onSearchingResult(resultStatus: SearchingResultStatus) {
        when (resultStatus) {
            SearchingResultStatus.SUCCESS -> {
                somethingWentWrongLayout.visibility = View.GONE
            }
            SearchingResultStatus.NOTHING_FOUND -> {
                somethingWentWrongLayout.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.ic_nothing_found)
                errorText.text = getString(R.string.search_nothing_found)
                updateButton.visibility = View.GONE
            }
            SearchingResultStatus.NO_INTERNET -> {
                tracks.clear()
                somethingWentWrongLayout.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.ic_no_internet)
                errorText.text = getString(R.string.search_no_internet)
                updateButton.visibility = View.VISIBLE
            }
        }
        trackAdapter.notifyDataSetChanged()
    }

    private fun setHistoryVisibility() {
        historyLayout.visibility = if (history.tracksList.size > 0) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SEARCH_TEXT, "")
        setHistoryVisibility()
    }

    private fun clearButtonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
}