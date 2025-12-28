package com.example.mobilefintechapp.profile.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val email: String = "",
    val fullName: String = "",
    val userId: String = ""
)

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val email = document.getString("email") ?: getCurrentUserEmail() ?: ""
                val fullName = document.getString("fullName") ?: ""

                Result.success(UserProfile(
                    email = email,
                    fullName = fullName,
                    userId = userId
                ))
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user profile", e)
            Result.failure(e)
        }
    }
}