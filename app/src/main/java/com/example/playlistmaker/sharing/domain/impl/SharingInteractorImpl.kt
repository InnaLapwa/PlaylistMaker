package com.example.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.SharingRepository
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val repository: SharingRepository
) : SharingInteractor {
    override fun shareApp(): Intent {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms(): Intent {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport(): Intent {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return repository.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return repository.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return repository.getTermsLink()
    }
}