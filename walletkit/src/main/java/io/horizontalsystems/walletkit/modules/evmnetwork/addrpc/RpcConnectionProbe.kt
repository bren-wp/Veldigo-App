package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import com.google.gson.JsonParser
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigInteger
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * A one-shot, read-only eth_chainId probe of a user-supplied HTTPS RPC.
 *
 * This client has NO logging interceptors, NO redirect following and bounded
 * timeouts/response size. It does not send wallet addresses or secret keys.
 * Never run the probe directly when Tor is configured: this independent client
 * has no proof it uses the wallet's Tor proxy and could leak the user's IP.
 */
internal class RpcConnectionProbe(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(7, TimeUnit.SECONDS)
        .readTimeout(7, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build(),
) {
    enum class Outcome {
        Verified, WrongChain, InvalidResponse, Unreachable, NotTestedTor,
        NotTestedTransport, NotTestedChain, InvalidUrl, UnsupportedAuth
    }

    data class Result(val outcome: Outcome, val reportedChainId: BigInteger? = null)

    suspend fun check(
        blockchainType: BlockchainType,
        url: String,
        basicAuth: String,
        torEnabled: Boolean,
    ): Result {
        val uri = RpcEndpointValidation.parse(url) ?: return Result(Outcome.InvalidUrl)
        val expected = RpcChainIdentityRules.expected(blockchainType)
            ?: return Result(Outcome.NotTestedChain)
        if (torEnabled) return Result(Outcome.NotTestedTor)
        if (uri.scheme != "https") return Result(Outcome.NotTestedTransport)

        val credentials = basicAuth.takeIf(String::isNotBlank)?.let {
            if ('\n' in it || '\r' in it || ':' !in it) return Result(Outcome.UnsupportedAuth)
            val user = it.substringBefore(':')
            val pass = it.substringAfter(':')
            Credentials.basic(user, pass)
        }

        return withContext(Dispatchers.IO) {
            probeHttps(uri, credentials, expected)
        }
    }

    private fun probeHttps(
        uri: URI,
        authorization: String?,
        expected: BigInteger,
    ): Result {
        val requestBuilder = Request.Builder()
            .url(uri.toString())
            .post(
                """{"jsonrpc":"2.0","method":"eth_chainId","params":[],"id":1}"""
                    .toRequestBody("application/json".toMediaType())
            )
            .header("Accept", "application/json")
        if (authorization != null) requestBuilder.header("Authorization", authorization)

        return try {
            client.newCall(requestBuilder.build()).execute().use { response ->
                if (!response.isSuccessful) return@use Result(Outcome.Unreachable)

                // Size limit prevents a misbehaving RPC returning enormous HTML,
                // a huge JSON payload, or unlimited data into wallet process RAM.
                val reader = response.body?.charStream()?.buffered()
                    ?: return@use Result(Outcome.InvalidResponse)
                val result = reader.use { stream ->
                    val chars = CharArray(8193)
                    var count = 0
                    while (count < chars.size) {
                        val n = stream.read(chars, count, chars.size - count)
                        if (n < 0) break
                        if (n == 0) break
                        count += n
                    }
                    if (count == 0 || count > 8192) return@use Result(Outcome.InvalidResponse)
                    String(chars, 0, count)
                }
                val reportedId = RpcChainIdResponse.parse(result)
                    ?: return@use Result(Outcome.InvalidResponse)
                if (RpcChainIdentityRules.matches(expected, reportedId)) {
                    Result(Outcome.Verified, reportedId)
                } else {
                    Result(Outcome.WrongChain, reportedId)
                }
            }
        } catch (_: Exception) {
            // Do not render or log exception text: it may include the URL,
            // authentication header, or embedded RPC API key.
            Result(Outcome.Unreachable)
        }
    }
}

/** Validate response framing as well as a properly formed positive EVM chain ID. */
internal object RpcChainIdResponse {
    fun parse(json: String): BigInteger? = runCatching {
        val obj = JsonParser.parseString(json).asJsonObject
        if (obj.get("jsonrpc")?.asString != "2.0") return@runCatching null
        if (obj.get("id")?.asInt != 1) return@runCatching null
        if (obj.has("error")) return@runCatching null
        RpcChainIdentityRules.parseHex(obj.get("result")?.asString)
    }.getOrNull()
}
