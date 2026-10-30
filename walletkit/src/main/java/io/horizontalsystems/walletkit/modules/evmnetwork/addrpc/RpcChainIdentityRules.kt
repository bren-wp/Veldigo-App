package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import io.horizontalsystems.marketkit.models.BlockchainType
import java.math.BigInteger

/**
 * Chain IDs for established EVM mainnets. Unknown chains deliberately have
 * no expected ID, so an RPC response cannot be labeled "verified" by guess.
 */
internal object RpcChainIdentityRules {
    fun expected(type: BlockchainType): BigInteger? = when (type) {
        BlockchainType.Ethereum -> BigInteger.ONE
        BlockchainType.BinanceSmartChain -> BigInteger.valueOf(56)
        BlockchainType.Polygon -> BigInteger.valueOf(137)
        BlockchainType.Avalanche -> BigInteger.valueOf(43114)
        BlockchainType.Optimism -> BigInteger.TEN
        BlockchainType.Base -> BigInteger.valueOf(8453)
        BlockchainType.ZkSync -> BigInteger.valueOf(324)
        BlockchainType.ArbitrumOne -> BigInteger.valueOf(42161)
        BlockchainType.Gnosis -> BigInteger.valueOf(100)
        BlockchainType.Fantom -> BigInteger.valueOf(250)
        else -> null
    }

    fun parseHex(value: String?): BigInteger? {
        if (value == null || !Regex("^0x[0-9a-fA-F]{1,32}$").matches(value)) return null
        return runCatching { BigInteger(value.substring(2), 16).takeIf { it > BigInteger.ZERO } }.getOrNull()
    }

    fun matches(expected: BigInteger?, reported: BigInteger): Boolean =
        expected != null && expected == reported
}
