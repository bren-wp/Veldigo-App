package io.horizontalsystems.walletkit.core.managers

import io.horizontalsystems.walletkit.core.ILocalStorage

class DonationShowManager(
    private val localStorage: ILocalStorage,
) {

    // Branded distribution has no donation program configured.
    fun shouldShow(): Boolean = false

    fun updateDonatePageShownDate() {
        localStorage.donateUsLastShownDate = System.currentTimeMillis()
    }

}
