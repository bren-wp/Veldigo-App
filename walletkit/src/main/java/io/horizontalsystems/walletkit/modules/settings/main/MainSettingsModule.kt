package io.horizontalsystems.walletkit.modules.settings.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.walletkit.core.App

object MainSettingsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainSettingsViewModel(
                App.backupManager,
                App.systemInfoManager,
                App.termsManager,
                App.pinComponent,
                App.accountManager,
                App.appConfigProvider,
            ) as T
    }
}
