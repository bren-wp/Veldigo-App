package io.horizontalsystems.walletkit.modules.nft

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verify opt-in NFT artwork input safety without making network calls. */
class NftGallerySafetyTest {
    @Test
    fun onlySameActiveAccountCanDisplayProviderReportedNfts() {
        assertTrue(NftGallerySafety.isSameAccount("account-a", "account-a"))
        assertFalse(NftGallerySafety.isSameAccount("account-a", "account-b"))
        assertFalse(NftGallerySafety.isSameAccount("", ""))
        assertFalse(NftGallerySafety.isSameAccount("account-a", null))
    }

    @Test
    fun thumbnailMustUseHttpsAndCannotReadLocalAssets() {
        assertNull(NftGallerySafety.safeRemoteImage(null))
        assertNull(NftGallerySafety.safeRemoteImage(""))
        assertNull(NftGallerySafety.safeRemoteImage("ipfs://QmExample"))
        assertNull(NftGallerySafety.safeRemoteImage("http://example.org/nft.png"))
        assertNull(NftGallerySafety.safeRemoteImage("file:///etc/passwd"))
        assertNull(NftGallerySafety.safeRemoteImage("https://127.0.0.1/private"))
        assertNull(NftGallerySafety.safeRemoteImage("https://192.168.1.5/image"))
        assertNull(NftGallerySafety.safeRemoteImage("https://10.0.0.8/image"))
        assertNull(NftGallerySafety.safeRemoteImage("https://172.16.0.2/image"))
        assertNull(NftGallerySafety.safeRemoteImage("https://localhost/pixel"))
        assertNull(NftGallerySafety.safeRemoteImage("https://wallet.local/image"))
        assertNull(NftGallerySafety.safeRemoteImage("https://user:pass@example.org/nft"))
    }

    @Test
    fun httpsPublicArtworkCanRenderWhenUserExplicitlyLoadsNfts() {
        val input = "https://cdn.example.org/collection/nft-123.png"
        assertEquals(input, NftGallerySafety.safeRemoteImage(input))
    }
}
