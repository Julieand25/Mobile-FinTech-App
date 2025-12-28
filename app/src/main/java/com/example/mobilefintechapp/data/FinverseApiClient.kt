package com.example.mobilefintechapp.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import java.util.Base64

class FinverseApiClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }

    // Exchange authorization code for access token
    suspend fun exchangeCodeForToken(authCode: String): Result<FinverseTokenResponse> {
        return try {
            val credentials = "${FinverseConfig.CLIENT_ID}:${FinverseConfig.CLIENT_SECRET}"
            val encodedCredentials = android.util.Base64.encodeToString(
                credentials.toByteArray(),
                android.util.Base64.NO_WRAP
            )

            val response: HttpResponse = client.post(FinverseConfig.TOKEN_URL) {
                header("Authorization", "Basic $encodedCredentials")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    "grant_type=authorization_code&code=$authCode&redirect_uri=${FinverseConfig.REDIRECT_URI}"
                )
            }

            Log.d("FinverseAPI", "Token response: ${response.status}")
            val tokenResponse = response.body<FinverseTokenResponse>()
            Result.success(tokenResponse)
        } catch (e: Exception) {
            Log.e("FinverseAPI", "Token exchange failed", e)
            Result.failure(e)
        }
    }

    // Fetch accounts
    suspend fun fetchAccounts(accessToken: String): Result<List<FinverseAccount>> {
        return try {
            val response: HttpResponse = client.get(FinverseConfig.ACCOUNTS_URL) {
                header("Authorization", "Bearer $accessToken")
            }

            Log.d("FinverseAPI", "Accounts response: ${response.status}")
            val accountResponse = response.body<FinverseAccountResponse>()
            Result.success(accountResponse.accounts)
        } catch (e: Exception) {
            Log.e("FinverseAPI", "Failed to fetch accounts", e)
            Result.failure(e)
        }
    }

    // Fetch transactions
    suspend fun fetchTransactions(
        accessToken: String,
        accountId: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<FinverseTransaction>> {
        return try {
            val response: HttpResponse = client.get(FinverseConfig.TRANSACTIONS_URL) {
                header("Authorization", "Bearer $accessToken")

                // Optional filters
                accountId?.let { parameter("account_id", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }

            Log.d("FinverseAPI", "Transactions response: ${response.status}")
            val transactionResponse = response.body<FinverseTransactionResponse>()
            Result.success(transactionResponse.transactions)
        } catch (e: Exception) {
            Log.e("FinverseAPI", "Failed to fetch transactions", e)
            Result.failure(e)
        }
    }
}