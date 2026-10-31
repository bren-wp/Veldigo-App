package io.horizontalsystems.walletkit.modules.pin.core

import io.horizontalsystems.walletkit.modules.main.MainModule.MainNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Regression coverage for a fully private wallet-only app. */
class LockGateTest {
    private val isLocked = MutableStateFlow(true)
    private val marketsTabEnabled = MutableStateFlow(true)
    private val gate = LockGate(isLocked, marketsTabEnabled, CoroutineScope(Dispatchers.Unconfined))

    @Test
    fun `cold start shows PIN before any tab reports itself`() {
        assertTrue(gate.showUnlock)
        assertFalse(gate.isRestricted)
        gate.currentPageAccessibleWhileLocked = true
        assertTrue(gate.showUnlock)
    }

    @Test
    fun `all tabs require PIN even with legacy public market preference`() {
        gate.currentPageAccessibleWhileLocked = true
        for (tab in MainNavigation.values()) {
            gate.selectedTab = tab
            assertTrue("Locked tab should require PIN: $tab", gate.showUnlock)
            assertFalse(gate.isTabAccessibleWhileLocked(tab))
        }
    }

    @Test
    fun `public legacy flags cannot expose balances when locked`() {
        gate.selectedTab = MainNavigation.Market
        gate.currentPageAccessibleWhileLocked = true
        gate.mainPageOnTop = false
        assertTrue(gate.showUnlock)

        marketsTabEnabled.value = false
        assertTrue(gate.showUnlock)
        marketsTabEnabled.value = true
        assertTrue(gate.showUnlock)
    }

    @Test
    fun `unlock closes keypad and locking always restores it`() {
        isLocked.value = false
        assertFalse(gate.showUnlock)
        isLocked.value = true
        assertTrue(gate.showUnlock)
    }

    @Test
    fun `protected shortcuts wait for a successful unlock`() {
        val opened = mutableListOf<String>()
        gate.requireUnlocked { opened.add("assets") }
        assertTrue(opened.isEmpty())
        assertTrue(gate.unlockRequested)
        assertTrue(gate.showUnlock)

        isLocked.value = false
        assertEquals(listOf("assets"), opened)
        assertFalse(gate.unlockRequested)
        assertFalse(gate.showUnlock)
    }

    @Test
    fun `cancelling a private shortcut does not open it on next unlock`() {
        var count = 0
        gate.requireUnlocked { count++ }
        gate.cancelUnlockRequest()
        isLocked.value = false
        assertEquals(0, count)
        assertFalse(gate.unlockRequested)
    }

    @Test
    fun `latest locked shortcut replaces prior request`() {
        val opened = mutableListOf<String>()
        gate.requireUnlocked { opened.add("assets") }
        gate.requireUnlocked { opened.add("activity") }
        isLocked.value = false
        assertEquals(listOf("activity"), opened)
    }

    @Test
    fun `successful unlock never replays protected action twice`() {
        var count = 0
        gate.requireUnlocked { count++ }
        isLocked.value = false
        isLocked.value = true
        isLocked.value = false
        assertEquals(1, count)
    }

    @Test
    fun `unlocked shortcut runs immediately`() {
        isLocked.value = false
        var count = 0
        gate.requireUnlocked { count++ }
        assertEquals(1, count)
        assertFalse(gate.unlockRequested)
    }

    @Test
    fun `throwing protected callback does not kill PIN collector`() {
        gate.requireUnlocked { throw IllegalStateException("test") }
        isLocked.value = false
        isLocked.value = true
        assertTrue(gate.showUnlock)

        var count = 0
        gate.requireUnlocked { count++ }
        isLocked.value = false
        assertEquals(1, count)
    }
}
