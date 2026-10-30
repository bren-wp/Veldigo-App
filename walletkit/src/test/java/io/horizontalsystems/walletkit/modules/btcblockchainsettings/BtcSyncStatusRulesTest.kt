package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import io.horizontalsystems.walletkit.core.AdapterState
import io.horizontalsystems.walletkit.modules.btcblockchainsettings.BtcSyncStatusRules.Phase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BtcSyncStatusRulesTest {
    @Test fun noActiveWalletDoesNotPretendToBeSynced() {
        val state = BtcSyncStatusRules.summarize(emptyList(), false)
        assertEquals(Phase.NoWallet, state.phase)
        assertEquals(0, state.wallets)
        assertNull(state.progress)
    }

    @Test fun noActiveAdapterNeverClaimsSuccessfulSynchronization() {
        val state = BtcSyncStatusRules.summarize(listOf(null), false)
        assertEquals(Phase.WaitingForAdapter, state.phase)
        assertEquals(0, state.adapters)
    }

    @Test fun waitingForTorDoesNotExposePrematureAdapterFailure() {
        val state = BtcSyncStatusRules.summarize(
            listOf(AdapterState.NotSynced(Exception("private endpoint token"))),
            torWaiting = true,
        )
        assertEquals(Phase.WaitingForTor, state.phase)
    }

    @Test fun oneRealAdapterCanReportRealProgressAndRemainingBlocks() {
        val state = BtcSyncStatusRules.summarize(
            listOf(AdapterState.Syncing(progress = 47, blocksRemained = 88L)),
            false,
        )
        assertEquals(Phase.Syncing, state.phase)
        assertEquals(47, state.progress)
        assertEquals(88L, state.remainingBlocks)
    }

    @Test fun malformedProgressCannotBeDisplayedAsARealPercentage() {
        for (progress in listOf(-1, 101, 1000)) {
            val state = BtcSyncStatusRules.summarize(
                listOf(AdapterState.Syncing(progress = progress)),
                false,
            )
            assertNull(state.progress)
        }
    }

    @Test fun severalWalletsNeverShowInventedAverageProgressOrFalseCompletion() {
        val state = BtcSyncStatusRules.summarize(
            listOf(AdapterState.Synced, AdapterState.Syncing(progress = 75)),
            false,
        )
        assertEquals(Phase.Syncing, state.phase)
        assertEquals(1, state.synced)
        assertEquals(2, state.wallets)
        assertNull(state.progress)
    }

    @Test fun allAdaptersMustFinishBeforeShowingSynced() {
        assertEquals(
            Phase.Synced,
            BtcSyncStatusRules.summarize(
                listOf(AdapterState.Synced, AdapterState.Synced), false
            ).phase,
        )
        assertEquals(
            Phase.WaitingForAdapter,
            BtcSyncStatusRules.summarize(
                listOf(AdapterState.Synced, null), false
            ).phase,
        )
    }

    @Test fun transactionSearchAndConnectionAreShownWithoutFakePercentage() {
        val searching = BtcSyncStatusRules.summarize(
            listOf(AdapterState.SearchingTxs(14)), false
        )
        assertEquals(Phase.SearchingTransactions, searching.phase)
        assertEquals(14, searching.transactionsFound)
        assertNull(searching.progress)

        val connecting = BtcSyncStatusRules.summarize(
            listOf(AdapterState.Connecting), false
        )
        assertEquals(Phase.Connecting, connecting.phase)
    }

    @Test fun anyFailedAdapterPreventsFalseSuccessAndNeverShowsErrorMessage() {
        val state = BtcSyncStatusRules.summarize(
            listOf(AdapterState.Synced, AdapterState.NotSynced(Exception("private-key-looking-secret"))),
            false,
        )
        assertEquals(Phase.Failed, state.phase)
        assertNull(state.progress)
    }
}
