package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.data.TrackDto
import com.example.playlistmaker.domain.TrackSearchingCallback
import com.example.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
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
    private lateinit var progressBar: ProgressBar

    private var isClickAllowed = true
    private var inputText = ""
    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private val trackRepository = App.instance.getTrackRepository()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { findTracks() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        history = SearchHistory(App.instance.getTrackStorage())

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
        progressBar = findViewById(R.id.search_progressBar)
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
                searchDebounce()
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
            if (clickDebounce()) {
                openPlayer(track)
            }
        }

        historyAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                openPlayer(track)
            }
        }

        trackAdapter.tracks = tracks
        searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchRecyclerView.adapter = trackAdapter

        historyAdapter.tracks = history.tracksList
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun findTracks() {
        if (inputSearch.text.isEmpty()) return


        progressBar.visibility = View.VISIBLE
        trackRepository.getTracks(inputSearch.text.toString(), object : TrackSearchingCallback {
            override fun onSuccess(tracksDto: ArrayList<TrackDto>) {
                progressBar.visibility = View.GONE

                tracks.clear()
                tracksDto.forEach {
                    tracks.add(convertTrackDto(it))
                }

                if (tracks.isEmpty()) onSearchingResult(SearchingResultStatus.NOTHING_FOUND)
                else onSearchingResult(SearchingResultStatus.SUCCESS)
            }

            override fun onFailure() {
                progressBar.visibility = View.GONE
                onSearchingResult(SearchingResultStatus.NO_INTERNET)
            }
        })
    }

    fun convertTrackDto(trackDto: TrackDto) = Track(
        trackName = trackDto.trackName,
        artistName = trackDto.artistName,
        trackTime = trackDto.trackTime,
        artworkUrl100 = trackDto.artworkUrl100,
        collectionName = trackDto.collectionName,
        releaseDate = trackDto.releaseDate,
        primaryGenreName = trackDto.primaryGenreName,
        country = trackDto.country,
        previewUrl = trackDto.previewUrl
    )

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

    private fun openPlayer(track: Track) {
        val player = Intent(this, PlayerActivity::class.java)
        player.putExtra("track", track)
        startActivity(player)
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

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
