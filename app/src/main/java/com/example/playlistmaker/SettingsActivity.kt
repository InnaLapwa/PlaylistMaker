package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backLayout = findViewById<FrameLayout>(R.id.fl_settings_back)

        backLayout.setOnClickListener {
            finish()
        }

        val shareLayout = findViewById<FrameLayout>(R.id.shareLayout)
        shareLayout.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
                startActivity(Intent.createChooser(this, "Share with:"))
            }
        }

        val supportLayout = findViewById<FrameLayout>(R.id.supportLayout)
        supportLayout.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.share_app_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
                startActivity(this)
            }
        }

        val userAgreementLayout = findViewById<FrameLayout>(R.id.userAgreementLayout)
        userAgreementLayout.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link))))
        }
    }
}