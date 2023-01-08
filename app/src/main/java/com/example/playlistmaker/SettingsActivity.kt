package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity: AppCompatActivity() {
    private lateinit var backLayout: FrameLayout
    private lateinit var shareLayout: FrameLayout
    private lateinit var supportLayout: FrameLayout
    private lateinit var userAgreementLayout: FrameLayout
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        backLayout = findViewById(R.id.fl_settings_back)
        shareLayout = findViewById(R.id.shareLayout)
        supportLayout = findViewById(R.id.supportLayout)
        userAgreementLayout = findViewById(R.id.userAgreementLayout)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        themeSwitcher.isChecked = sharedPrefs.getBoolean(THEME_SWITCHER, false)
    }

    private fun setListeners() {
        backLayout.setOnClickListener {
            finish()
        }

        shareLayout.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
                startActivity(Intent.createChooser(this, "Share with:"))
            }
        }

        supportLayout.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.share_app_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
                startActivity(this)
            }
        }

        userAgreementLayout.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link))))
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(THEME_SWITCHER, checked)
                .apply()
        }
    }

}