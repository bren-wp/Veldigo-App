package io.horizontalsystems.walletkit.modules.balance.token

import io.horizontalsystems.chartview.models.ChartPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VeldigoAssetChartRulesTest {
    @Test
    fun emptyAndSinglePointSeriesHaveNoPercentageChange() {
        assertNull(VeldigoAssetChartRules.changePercent(emptyList()))
        assertNull(VeldigoAssetChartRules.changePercent(listOf(ChartPoint(25f, 100L))))
    }

    @Test
    fun chartChangeComesFromTheProvidedPricesOnly() {
        val points = listOf(ChartPoint(100f, 1L), ChartPoint(105f, 2L))
        assertEquals(5f, VeldigoAssetChartRules.changePercent(points)!!, 0.001f)
    }

    @Test
    fun invalidAndDuplicatedPointsAreExcludedAndOrdered() {
        val points = listOf(
            ChartPoint(130f, 30L),
            ChartPoint(Float.NaN, 20L),
            ChartPoint(110f, 10L),
            ChartPoint(120f, 30L),
            ChartPoint(Float.POSITIVE_INFINITY, 40L),
        )
        val valid = VeldigoAssetChartRules.normalizedPoints(points)
        assertEquals(listOf(10L, 30L), valid.map { it.timestamp })
        assertEquals(110f, valid[0].value, 0f)
        assertEquals(130f, valid[1].value, 0f)
    }

    @Test
    fun missingValidStartPriceDoesNotInventAChange() {
        assertNull(VeldigoAssetChartRules.changePercent(listOf(
            ChartPoint(0f, 10L),
            ChartPoint(5f, 20L),
        )))
    }
}
