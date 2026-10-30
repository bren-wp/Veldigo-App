# VELDIGO Wallet — blockchain integration verification matrix

**This is a code inventory, not a list of production-certified networks.** Finding an adapter module is insufficient to demonstrate actual derived addresses, signing, correct fees, broadcasts, transaction histories, privacy or chain safety.

| Network / asset | Relevant code inventory | Integration state |
|---|---|---|
| Bitcoin (BTC) | `walletkit-chain-bitcoin` | Adapter code present; end-to-end **unverified** |
| Ethereum (ETH), ERC-20 | `walletkit-chain-evm` | Adapter code present; network/token validation **unverified** |
| BNB Smart Chain, BEP-20 | Shared EVM adapter may apply | Specific chain support **unverified** |
| Polygon | Shared EVM adapter may apply | Specific chain support **unverified** |
| Arbitrum, Optimism, Base | Shared EVM adapter may apply | Network/fee behavior **unverified** |
| Avalanche | Shared EVM adapter may apply | Network/fee behavior **unverified** |
| Solana (SOL), SPL | `walletkit-chain-solana` | Adapter code present; end-to-end **unverified** |
| TRON (TRX), TRC-20 | `walletkit-chain-tron` | Adapter code present; end-to-end **unverified** |
| Litecoin (LTC), Dogecoin (DOGE), Bitcoin Cash (BCH) | BTC-related shared code may apply | Coin-specific adapter capability **unverified** |
| XRP | `walletkit-chain-xrp` | Adapter code present; end-to-end **unverified** |
| Cardano (ADA) | No dedicated chain module found in root module inventory | **Not confirmed** — do not advertise |
| Stellar (XLM) | `walletkit-chain-stellar` | Adapter code present; end-to-end **unverified** |
| Polkadot (DOT) | No dedicated chain module found in root module inventory | **Not confirmed** — do not advertise |
| Cosmos (ATOM) | No dedicated chain module found in root module inventory | **Not confirmed** — do not advertise |
| TON | `walletkit-chain-ton` | Adapter code present; end-to-end **unverified** |
| NEAR | `walletkit-chain-near` | Adapter code present; end-to-end **unverified** |
| Monero, Zcash, Zano, Thorchain | Dedicated walletkit-chain modules | Adapter code present; end-to-end **unverified** |

## Mandatory promotion gate
For each chain, independently test: BIP/chain-native derivation and restore vectors; account isolation; address encoding/checksum; balances and confirmations; nonce or UTXO selection; estimation and max-send fee reserve; local signing; testnet broadcast/receipt; pending/failed/replaced transaction history; RPC failures; malicious metadata/decimals; and no leakage of keys, recovery words or sensitive data.

**No network has been upgraded to certified/supported in this document.** Before any product UI advertises a new chain for Send/Receive, verify that its real adapters pass each required operation; unsupported networks must never be represented as live.
