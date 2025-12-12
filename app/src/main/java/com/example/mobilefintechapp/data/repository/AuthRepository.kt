package com.example.mobilefintechapp.data.repository

import android.util.Log
import com.example.mobilefintechapp.data.model.UserRegistration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    /**
     * Register a new user with email and password
     */
    suspend fun registerUser(userRegistration: UserRegistration): Result<String> {
        return try {
            // Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(
                userRegistration.email,
                userRegistration.password
            ).await()

            val user = authResult.user
            if (user == null) {
                return Result.failure(Exception("Failed to create user"))
            }

            // Update user profile with full name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userRegistration.fullName)
                .build()

            user.updateProfile(profileUpdates).await()

            // Store additional user data in Firestore
            val userData = hashMapOf(
                "uid" to user.uid,
                "fullName" to userRegistration.fullName,
                "email" to userRegistration.email,
                "createdAt" to System.currentTimeMillis(),
                "emailVerified" to true, // Already verified via OTP
                "profileComplete" to false
            )

            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userData)
                .await()

            Log.d("AuthRepository", "User registered successfully: ${user.uid}")
            Result.success(user.uid)

        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "Firebase Auth Error: ${e.message}", e)
            val errorMessage = when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered"
                "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters"
                "ERROR_INVALID_EMAIL" -> "Invalid email address"
                else -> e.message ?: "Registration failed"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Check if email already exists
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            methods.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        auth.signOut()
    }
}