package com.example.mobilefintechapp.homepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.goals.Goal
import com.example.mobilefintechapp.profile.repository.UserProfile
import com.example.mobilefintechapp.profile.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomepageViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    init {
        observeGoals()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoadingProfile.value = true

            userRepository.getUserProfile()
                .onSuccess { profile ->
                    _userProfile.value = profile
                    Log.d("HomepageViewModel", "User profile loaded: ${profile.fullName}")
                }
                .onFailure { exception ->
                    Log.e("HomepageViewModel", "Error loading profile", exception)
                }

            _isLoadingProfile.value = false
        }
    }

    private fun observeGoals() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let {
                    _goals.value = it.documents.mapNotNull { doc ->
                        doc.toObject(Goal::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }
}