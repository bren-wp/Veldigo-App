package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

/** A wrong chain ID is never an overridable "network unavailable" warning. */
internal object RpcConnectionUiRules {
    fun canAcknowledge(outcome: RpcConnectionProbe.Outcome?): Boolean =
        outcome != null && outcome !in setOf(
            RpcConnectionProbe.Outcome.Verified,
            RpcConnectionProbe.Outcome.WrongChain,
            RpcConnectionProbe.Outcome.InvalidUrl,
        )

    fun canSave(
        endpointPresent: Boolean,
        checking: Boolean,
        result: RpcConnectionProbe.Outcome?,
        acknowledged: Boolean,
    ): Boolean {
        if (!endpointPresent || checking) return false
        return when (result) {
            RpcConnectionProbe.Outcome.Verified -> true
            else -> canAcknowledge(result) && acknowledged
        }
    }
}
