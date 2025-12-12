package com.example.mobilefintechapp.data.model

sealed class OtpVerificationResult {
    object Success : OtpVerificationResult()
    data class Error(val message: String) : OtpVerificationResult()
    object InvalidOtp : OtpVerificationResult()
    object Expired : OtpVerificationResult()
    object TooManyAttempts : OtpVerificationResult()
}