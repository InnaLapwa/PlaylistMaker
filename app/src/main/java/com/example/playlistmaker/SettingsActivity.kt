package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
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
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
            startActivity(Intent.createChooser(shareIntent, "Share with:"))
        }

        val supportLayout = findViewById<FrameLayout>(R.id.supportLayout)
        supportLayout.setOnClickListener {
            val subject = getString(R.string.share_app_subject)
            val message = getString(R.string.share_app_message)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.share_app_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        val userAgreementLayout = findViewById<FrameLayout>(R.id.userAgreementLayout)
        userAgreementLayout.setOnClickListener {
            val userAgreementAddress = Uri.parse(getString(R.string.user_agreement_link))
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, userAgreementAddress)
            startActivity(userAgreementIntent)
        }
    }
}