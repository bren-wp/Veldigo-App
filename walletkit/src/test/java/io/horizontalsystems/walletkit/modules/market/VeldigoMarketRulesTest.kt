package io.horizontalsystems.walletkit.modules.market

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Screen 16 cannot substitute artwork prices or a simulated asset catalogue. */
class VeldigoMarketRulesTest {
    @Test
    fun searchingMatchesRealCodeAndNameRegardlessOfCase() {
        assertTrue(VeldigoMarketRules.matchesQuery("BTC", "Bitcoin", "btc"))
        assertTrue(VeldigoMarketRules.matchesQuery("BTC", "Bitcoin", " BITCOIN "))
        assertTrue(VeldigoMarketRules.matchesQuery("ETH", "Ethereum", "ethereum"))
        assertFalse(VeldigoMarketRules.matchesQuery("ETH", "Ethereum", "sol"))
        assertTrue(VeldigoMarketRules.matchesQuery("ETH", "Ethereum", ""))
    }

    @Test
    fun unknownChangeHasNoMadeUpPercentage() {
        assertNull(VeldigoMarketRules.changeLabel(null))
    }

    @Test
    fun actual24hChangeFormatsSignAndValueWithoutArtDefaults() {
        assertEquals("+2.4%", VeldigoMarketRules.changeLabel(BigDecimal("2.4")))
        assertEquals("-1.4%", VeldigoMarketRules.changeLabel(BigDecimal("-1.4")))
        assertEquals("0.0%", VeldigoMarketRules.changeLabel(BigDecimal.ZERO))
    }

    @Test
    fun filterTabsUseAuthenticMarketSortFields() {
        assertEquals(SortingField.HighestCap, VeldigoMarketFilter.All.sortingField)
        assertEquals(SortingField.TopGainers, VeldigoMarketFilter.TopGainers.sortingField)
        assertEquals(SortingField.TopLosers, VeldigoMarketFilter.TopLosers.sortingField)
    }
}
