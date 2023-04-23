package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return R.string.share_app_link.toString()
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            R.string.share_app_email.toString(),
            R.string.share_app_subject.toString(),
            R.string.share_app_message.toString()
        )
    }

    private fun getTermsLink(): String {
        return R.string.user_agreement_link.toString()
    }
}