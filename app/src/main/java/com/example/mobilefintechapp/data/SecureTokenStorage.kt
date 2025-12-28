package com.example.mobilefintechapp.data

import android.content.Context
import android.content.SharedPreferences

class SecureTokenStorage(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "finverse_prefs",
        Context.MODE_PRIVATE
    )

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveAccountId(accountId: String) {
        sharedPreferences.edit().putString("account_id", accountId).apply()
    }

    fun getAccountId(): String? {
        return sharedPreferences.getString("account_id", null)
    }

    fun saveBankName(bankName: String) {
        sharedPreferences.edit().putString("bank_name", bankName).apply()
    }

    fun getBankName(): String? {
        return sharedPreferences.getString("bank_name", null)
    }

    fun saveAccountMask(mask: String) {
        sharedPreferences.edit().putString("account_mask", mask).apply()
    }

    fun getAccountMask(): String? {
        return sharedPreferences.getString("account_mask", null)
    }

    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }

    fun hasValidToken(): Boolean {
        return getAccessToken() != null
    }
}