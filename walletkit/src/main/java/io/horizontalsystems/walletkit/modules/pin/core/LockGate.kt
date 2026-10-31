package io.horizontalsystems.walletkit.modules.pin.core

import io.horizontalsystems.walletkit.modules.main.MainModule.MainNavigation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Wallet-only lock gate.
 *
 * All screens contain potentially sensitive financial data. The legacy Market
 * read-only exception must never be allowed to bypass the wallet PIN, even if
 * an upgraded user still has the historical "markets tab enabled" preference.
 *
 * The original constructor and public state are preserved for callers that
 * still migrate legacy settings. No private data is exposed while locked.
 */
class LockGate(
    private val isLockedFlow: StateFlow<Boolean>,
    @Suppress("UNUSED_PARAMETER") marketsTabEnabledFlow: StateFlow<Boolean>,
    scope: CoroutineScope,
) {
    private val _showUnlockFlow = MutableStateFlow(isLockedFlow.value)
    val showUnlockFlow: StateFlow<Boolean> = _showUnlockFlow.asStateFlow()
    val showUnlock: Boolean get() = _showUnlockFlow.value

    private val _unlockRequestedFlow = MutableStateFlow(false)
    val unlockRequestedFlow: StateFlow<Boolean> = _unlockRequestedFlow.asStateFlow()
    val unlockRequested: Boolean get() = _unlockRequestedFlow.value

    private var pendingAction: (() -> Unit)? = null

    val isLocked: Boolean get() = isLockedFlow.value

    /** Kept for existing callers; wallet-only mode has no public Market state. */
    val isRestricted: Boolean get() = false

    var selectedTab: MainNavigation? = null
        set(value) {
            field = value
            recompute()
        }

    var currentPageAccessibleWhileLocked: Boolean = false
        set(value) {
            field = value
            recompute()
        }

    var mainPageOnTop: Boolean = true

    init {
        scope.launch {
            isLockedFlow.collect { locked ->
                if (!locked) {
                    val action = pendingAction
                    pendingAction = null
                    _unlockRequestedFlow.value = false
                    recompute()
                    action?.let { runDeferred(it) }
                } else {
                    recompute()
                }
            }
        }
    }

    /** No wallet, settings, activity or legacy Market tab is public when locked. */
    @Suppress("UNUSED_PARAMETER")
    fun isTabAccessibleWhileLocked(tab: MainNavigation): Boolean = false

    /**
     * Runs the protected action immediately when unlocked. Otherwise the latest
     * request is deferred until the next successful unlock and may be cancelled.
     */
    fun requireUnlocked(action: () -> Unit) {
        if (!isLocked) {
            action()
            return
        }
        pendingAction = action
        _unlockRequestedFlow.value = true
        recompute()
    }

    fun cancelUnlockRequest() {
        pendingAction = null
        _unlockRequestedFlow.value = false
        recompute()
    }

    // Compatibility methods retained for ViewModels compiled against the old gate.
    // There is no safe public page to switch to while locked.
    @Suppress("UNUSED_PARAMETER")
    fun addRestrictedLockListener(listener: () -> Unit) = Unit

    @Suppress("UNUSED_PARAMETER")
    fun removeRestrictedLockListener(listener: () -> Unit) = Unit

    private fun runDeferred(block: () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // A failing destination must not kill the lock-state collector.
            Timber.e(e, "LockGate protected action failed")
        }
    }

    private fun recompute() {
        // Legacy Markets preference, selected tab and screen flags never bypass
        // the PIN. Lock state is the sole authority for rendering the overlay.
        _showUnlockFlow.value = isLocked
    }
}
