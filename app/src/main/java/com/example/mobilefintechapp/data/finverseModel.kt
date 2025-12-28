package com.example.mobilefintechapp.data

import com.google.gson.annotations.SerializedName

// Token response from Finverse
data class FinverseTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String?
)

// Account information
data class FinverseAccount(
    @SerializedName("id")
    val id: String,
    @SerializedName("institution_id")
    val institutionId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("subtype")
    val subtype: String?,
    @SerializedName("mask")
    val mask: String?, // Last 4 digits
    @SerializedName("balance")
    val balance: FinverseBalance?
)

data class FinverseBalance(
    @SerializedName("available")
    val available: Double?,
    @SerializedName("current")
    val current: Double?,
    @SerializedName("currency")
    val currency: String
)

// Transaction from Finverse
data class FinverseTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("category")
    val category: List<String>?,
    @SerializedName("payment_channel")
    val paymentChannel: String?,
    @SerializedName("pending")
    val pending: Boolean,
    @SerializedName("transaction_type")
    val transactionType: String?,
    @SerializedName("iso_currency_code")
    val isoCurrencyCode: String?
)

// Response wrapper
data class FinverseTransactionResponse(
    @SerializedName("transactions")
    val transactions: List<FinverseTransaction>,
    @SerializedName("total")
    val total: Int
)

data class FinverseAccountResponse(
    @SerializedName("accounts")
    val accounts: List<FinverseAccount>
)