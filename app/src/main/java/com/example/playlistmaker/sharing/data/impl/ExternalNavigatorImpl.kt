package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(val context: Context): ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            context.startActivity(Intent.createChooser(this, "Share with:"))
        }
    }

    override fun openLink(termsLink: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)))
    }

    override fun openEmail(supportEmailData: EmailData) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
            context.startActivity(this)
        }
    }
}