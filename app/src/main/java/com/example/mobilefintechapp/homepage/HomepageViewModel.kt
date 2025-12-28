package com.example.mobilefintechapp.homepage

import androidx.lifecycle.ViewModel
import com.example.mobilefintechapp.goals.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomepageViewModel : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    init {
        observeGoals()
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