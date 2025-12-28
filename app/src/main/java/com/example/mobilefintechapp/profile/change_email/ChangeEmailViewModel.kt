package com.example.mobilefintechapp.profile.change_email

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChangeEmailViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentEmail = MutableStateFlow("")
    val currentEmail: StateFlow<String> = _currentEmail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _verificationEmailSent = MutableStateFlow(false)
    val verificationEmailSent: StateFlow<Boolean> = _verificationEmailSent.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Store password and new email for re-authentication
    private var storedPassword: String? = null
    private var pendingNewEmail: String? = null

    init {
        loadCurrentEmail()
    }

    private fun loadCurrentEmail() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val document = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val email = document.getString("email")
                        ?: auth.currentUser?.email
                        ?: ""

                    _currentEmail.value = email
                    Log.d("ChangeEmailViewModel", "Current email loaded: $email")
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                Log.e("ChangeEmailViewModel", "Error loading email", e)
                _errorMessage.value = "Failed to load current email"
                _currentEmail.value = auth.currentUser?.email ?: ""
            }

            _isLoading.value = false
        }
    }

    fun sendVerificationEmail(newEmail: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val user = auth.currentUser
                val currentEmail = user?.email

                if (user == null || currentEmail == null) {
                    _errorMessage.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }

                // Store password and new email
                storedPassword = password
                pendingNewEmail = newEmail

                Log.d("ChangeEmailViewModel", "🔄 Re-authenticating user...")

                // Re-authenticate with current email and password
                val credential = EmailAuthProvider.getCredential(currentEmail, password)
                user.reauthenticate(credential).await()
                Log.d("ChangeEmailViewModel", "✅ Re-authenticated")

                // Send verification email to new address
                Log.d("ChangeEmailViewModel", "📧 Sending verification email to: $newEmail")
                user.verifyBeforeUpdateEmail(newEmail).await()
                Log.d("ChangeEmailViewModel", "✅ Verification email sent!")

                // Store pending email in Firestore
                firestore.collection("users")
                    .document(user.uid)
                    .update("pendingEmail", newEmail)
                    .await()

                _verificationEmailSent.value = true
                Log.d("ChangeEmailViewModel", "✅ Process complete!")

            } catch (e: Exception) {
                Log.e("ChangeEmailViewModel", "❌ Error: ${e.message}", e)
                _errorMessage.value = "Failed to send verification email: ${e.message}"
                storedPassword = null
                pendingNewEmail = null
            }

            _isLoading.value = false
        }
    }

    fun checkEmailUpdated() {
        viewModelScope.launch {
            try {
                var user = auth.currentUser

                // If user is logged out, try to re-login with new email
                if (user == null && pendingNewEmail != null && storedPassword != null) {
                    Log.d("ChangeEmailViewModel", "👤 User logged out, re-logging in with new email...")

                    try {
                        // Try login with new email
                        auth.signInWithEmailAndPassword(pendingNewEmail!!, storedPassword!!)
                            .await()

                        user = auth.currentUser
                        Log.d("ChangeEmailViewModel", "✅ Re-logged in successfully!")
                    } catch (e: Exception) {
                        Log.e("ChangeEmailViewModel", "❌ Re-login failed: ${e.message}")
                        // Email not verified yet, continue waiting
                        return@launch
                    }
                }

                if (user == null) {
                    Log.e("ChangeEmailViewModel", "❌ User is null")
                    return@launch
                }

                Log.d("ChangeEmailViewModel", "🔄 Reloading user from Firebase...")
                user.reload().await()

                val updatedEmail = user.email
                Log.d("ChangeEmailViewModel", "📧 Current email: $updatedEmail")
                Log.d("ChangeEmailViewModel", "📧 Pending email: $pendingNewEmail")

                if (updatedEmail == pendingNewEmail) {
                    // Email was updated!
                    Log.d("ChangeEmailViewModel", "✅ Email MATCHED! Updating Firestore...")

                    firestore.collection("users")
                        .document(user.uid)
                        .update(
                            mapOf(
                                "email" to updatedEmail,
                                "pendingEmail" to null
                            )
                        )
                        .await()

                    Log.d("ChangeEmailViewModel", "✅ Firestore updated")

                    _currentEmail.value = updatedEmail!!
                    _successMessage.value = "Email changed successfully!"

                    // Clear sensitive data
                    pendingNewEmail = null
                    storedPassword = null

                    Log.d("ChangeEmailViewModel", "✅✅✅ ALL DONE!")
                } else {
                    Log.d("ChangeEmailViewModel", "⏳ Email not updated yet. Current: $updatedEmail, Pending: $pendingNewEmail")
                }
            } catch (e: Exception) {
                Log.e("ChangeEmailViewModel", "❌ Error checking email: ${e.message}", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun resetVerificationEmailSent() {
        _verificationEmailSent.value = false
    }

    fun getPendingEmail(): String? = pendingNewEmail
}