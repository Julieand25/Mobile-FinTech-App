package com.example.mobilefintechapp.data.model

data class OtpRequest(
    val email: String,
    val otpType: OtpType,
    val metadata: Map<String, String> = emptyMap() // For additional data like userId, etc.
)