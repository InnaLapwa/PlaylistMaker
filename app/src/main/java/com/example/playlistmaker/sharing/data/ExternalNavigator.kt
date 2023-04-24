package com.example.playlistmaker.sharing.data

import android.content.Intent
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: String): Intent
    fun openLink(termsLink: String): Intent
    fun openEmail(supportEmailData: EmailData): Intent
}