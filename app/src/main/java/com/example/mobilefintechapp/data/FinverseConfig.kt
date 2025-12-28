package com.example.mobilefintechapp.data

object FinverseConfig {
    // ✅ Your REAL credentials from Finverse
    const val CLIENT_ID = "01KC8A639HPEPCFWYTT1CD4QQ2"
    const val CLIENT_SECRET = "fv-c-1765510679-38d18c4cb7af313275e90b44d6f048524243c897"

    // Sandbox/Test environment URLs
    const val BASE_URL = "https://api.sandbox.finverse.net"
    const val AUTH_URL = "$BASE_URL/oauth/authorize"
    const val TOKEN_URL = "$BASE_URL/oauth/token"
    const val TRANSACTIONS_URL = "$BASE_URL/v1/transactions"
    const val ACCOUNTS_URL = "$BASE_URL/v1/accounts"

    // Your app's redirect URI (must match Finverse dashboard)
    const val REDIRECT_URI = "halalfinance://callback"
}