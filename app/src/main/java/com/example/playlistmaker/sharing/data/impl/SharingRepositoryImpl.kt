package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.SharingRepository
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingRepositoryImpl(val context: Context): SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.share_app_link)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.share_app_email),
            context.getString(R.string.share_app_subject),
            context.getString(R.string.share_app_message)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.user_agreement_link)
    }
}