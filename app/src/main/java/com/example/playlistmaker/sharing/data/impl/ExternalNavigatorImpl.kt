package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(val context: Context): ExternalNavigator {
    override fun shareLink(shareAppLink: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        return Intent.createChooser(intent, "Share with:")
    }

    override fun openLink(termsLink: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(termsLink)
        return Intent.createChooser(intent, "Open in:")
    }

    override fun openEmail(supportEmailData: EmailData): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        intent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        intent.putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
        return intent
    }
}