// data/repository/OtpRepository.kt
package com.example.mobilefintechapp.data.repository

import android.util.Log
import com.example.mobilefintechapp.data.model.OtpType
import com.example.mobilefintechapp.data.model.OtpVerificationResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.*
import kotlin.random.Random

class OtpRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    companion object {
        private const val TAG = "OtpRepository"
        private const val OTP_COLLECTION = "otp_verifications"
        private const val OTP_VALIDITY_MINUTES = 5
        private const val MAX_ATTEMPTS = 5
        private const val LOCK_DURATION_HOURS = 8
        private const val TIMEOUT_SECONDS = 30L
    }

    /**
     * Generate and send OTP to user's email
     */
    suspend fun sendOtp(email: String, otpType: OtpType = OtpType.SIGN_UP): Result<String> {
        Log.d(TAG, "==========================================")
        Log.d(TAG, "sendOtp() called")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "OTP Type: $otpType")
        Log.d(TAG, "==========================================")

        return try {
            // Generate 6-digit OTP
            val otp = generateOtp()
            Log.d(TAG, "Generated OTP: $otp")

            val expiryTime = Calendar.getInstance().apply {
                add(Calendar.MINUTE, OTP_VALIDITY_MINUTES)
            }.timeInMillis

            // Store OTP in Firestore with timeout
            Log.d(TAG, "Storing OTP in Firestore...")
            val otpData = hashMapOf(
                "otp" to otp,
                "email" to email,
                "otpType" to otpType.name,
                "createdAt" to System.currentTimeMillis(),
                "expiryTime" to expiryTime,
                "attempts" to 0,
                "verified" to false
            )

            try {
                withTimeout(TIMEOUT_SECONDS * 1000) {
                    firestore.collection(OTP_COLLECTION)
                        .document(email)
                        .set(otpData)
                        .await()
                }
                Log.d(TAG, "✅ OTP stored in Firestore successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to store OTP in Firestore: ${e.message}", e)
                // Continue anyway - we'll still try to send the email
            }

            // IMPORTANT: Send email IMMEDIATELY after Firestore save
            Log.d(TAG, "==========================================")
            Log.d(TAG, "NOW calling Firebase Cloud Function...")
            Log.d(TAG, "==========================================")

            // Send email using Firebase Cloud Function with timeout
            try {
                val data = hashMapOf(
                    "email" to email,
                    "otp" to otp
                )

                Log.d(TAG, "Creating callable for 'sendOtpEmail'...")
                val callable = functions.getHttpsCallable("sendOtpEmail")
                Log.d(TAG, "Callable created, calling with timeout...")

                val result = withTimeout(TIMEOUT_SECONDS * 1000) {
                    callable.call(data).await()
                }

                Log.d(TAG, "✅ Cloud Function returned successfully!")
                Log.d(TAG, "Result: ${result.data}")

            } catch (e: Exception) {
                Log.e(TAG, "==========================================")
                Log.e(TAG, "❌ ERROR calling Cloud Function")
                Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Error message: ${e.message}")
                Log.e(TAG, "Error cause: ${e.cause}")
                e.printStackTrace()
                Log.e(TAG, "==========================================")

                // Don't fail the whole operation, but log it
                // The OTP is still in Firestore, user can still verify
            }

            // For development/testing - log OTP
            Log.d(TAG, "==========================================")
            Log.d(TAG, "📧 OTP FOR TESTING: $otp")
            Log.d(TAG, "📧 EMAIL: $email")
            Log.d(TAG, "==========================================")

            Result.success(otp)

        } catch (e: Exception) {
            Log.e(TAG, "==========================================")
            Log.e(TAG, "❌ FATAL ERROR in sendOtp()")
            Log.e(TAG, "Error: ${e.message}")
            e.printStackTrace()
            Log.e(TAG, "==========================================")
            Result.failure(e)
        }
    }

    /**
     * Verify the OTP entered by user
     */
    suspend fun verifyOtp(email: String, enteredOtp: String, otpType: OtpType): OtpVerificationResult {
        Log.d(TAG, "verifyOtp() called for email: $email, entered: $enteredOtp")

        return try {
            val document = firestore.collection(OTP_COLLECTION)
                .document(email)
                .get()
                .await()

            if (!document.exists()) {
                Log.e(TAG, "No OTP document found for email: $email")
                return OtpVerificationResult.Error("No OTP found. Please request a new one.")
            }

            val storedOtp = document.getString("otp")
            val storedOtpType = document.getString("otpType")
            val expiryTime = document.getLong("expiryTime") ?: 0L
            val attempts = document.getLong("attempts")?.toInt() ?: 0
            val verified = document.getBoolean("verified") ?: false

            Log.d(TAG, "Stored OTP: $storedOtp, Entered: $enteredOtp")

            // Check if OTP type matches
            if (storedOtpType != otpType.name) {
                return OtpVerificationResult.Error("Invalid OTP type")
            }

            // Check if already verified
            if (verified) {
                return OtpVerificationResult.Error("OTP already used")
            }

            // Check if expired
            if (System.currentTimeMillis() > expiryTime) {
                Log.e(TAG, "OTP expired")
                return OtpVerificationResult.Expired
            }

            // Check attempts
            if (attempts >= MAX_ATTEMPTS) {
                Log.e(TAG, "Too many attempts")
                return OtpVerificationResult.TooManyAttempts
            }

            // Verify OTP
            if (storedOtp == enteredOtp) {
                Log.d(TAG, "✅ OTP verified successfully!")
                // Mark as verified
                firestore.collection(OTP_COLLECTION)
                    .document(email)
                    .update("verified", true)
                    .await()

                return OtpVerificationResult.Success
            } else {
                Log.e(TAG, "❌ OTP mismatch")
                // Increment attempts
                firestore.collection(OTP_COLLECTION)
                    .document(email)
                    .update("attempts", attempts + 1)
                    .await()

                val attemptsLeft = MAX_ATTEMPTS - (attempts + 1)
                if (attemptsLeft <= 0) {
                    return OtpVerificationResult.TooManyAttempts
                }

                return OtpVerificationResult.InvalidOtp
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying OTP", e)
            OtpVerificationResult.Error(e.message ?: "Verification failed")
        }
    }

    /**
     * Get remaining attempts for an email
     */
    suspend fun getRemainingAttempts(email: String): Int {
        return try {
            val document = firestore.collection(OTP_COLLECTION)
                .document(email)
                .get()
                .await()

            if (!document.exists()) {
                return MAX_ATTEMPTS
            }

            val attempts = document.getLong("attempts")?.toInt() ?: 0
            MAX_ATTEMPTS - attempts
        } catch (e: Exception) {
            MAX_ATTEMPTS
        }
    }

    /**
     * Check if email is locked due to too many attempts
     */
    suspend fun isEmailLocked(email: String): Pair<Boolean, Long?> {
        return try {
            val document = firestore.collection(OTP_COLLECTION)
                .document(email)
                .get()
                .await()

            if (!document.exists()) {
                return Pair(false, null)
            }

            val attempts = document.getLong("attempts")?.toInt() ?: 0
            val lastAttemptTime = document.getLong("createdAt") ?: 0L

            if (attempts >= MAX_ATTEMPTS) {
                val lockEndTime = lastAttemptTime + (LOCK_DURATION_HOURS * 60 * 60 * 1000)
                val isLocked = System.currentTimeMillis() < lockEndTime
                return Pair(isLocked, if (isLocked) lockEndTime else null)
            }

            Pair(false, null)
        } catch (e: Exception) {
            Pair(false, null)
        }
    }

    /**
     * Generate a random 6-digit OTP
     */
    private fun generateOtp(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    /**
     * Delete OTP document after successful verification or expiry
     */
    suspend fun deleteOtp(email: String) {
        try {
            firestore.collection(OTP_COLLECTION)
                .document(email)
                .delete()
                .await()
            Log.d(TAG, "OTP deleted for email: $email")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting OTP", e)
        }
    }
}